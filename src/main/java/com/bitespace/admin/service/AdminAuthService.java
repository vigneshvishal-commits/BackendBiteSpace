package com.bitespace.admin.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitespace.admin.dto.AdminChangePasswordRequest;
import com.bitespace.admin.dto.AdminLoginRequest;
import com.bitespace.admin.dto.AuthResponse;
import com.bitespace.admin.model.AdminUser;
import com.bitespace.admin.model.PasswordResetToken;
import com.bitespace.admin.repository.AdminUserRepository;
import com.bitespace.admin.repository.PasswordResetTokenRepository;
import com.bitespace.admin.security.JwtUtil;
import com.bitespace.common.model.UserType;

import jakarta.mail.MessagingException;

@Service
public class AdminAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthResponse loginAdmin(AdminLoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            AdminUser adminUser = adminUserRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Admin user not found after authentication!"));

            // --- CRITICAL CHANGE HERE ---
            // Pass userDetails.getUsername() (which is the email in this case) as the username
            return new AuthResponse(jwt, userDetails.getUsername(), adminUser.isInitialPassword(), userDetails.getUsername());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid admin username or password.");
        } catch (Exception e) {
            throw new RuntimeException("Admin authentication failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void changeAdminPassword(String username, AdminChangePasswordRequest request) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));

        if (!passwordEncoder.matches(request.getOldPassword(), adminUser.getPassword())) {
            throw new BadCredentialsException("Invalid old password.");
        }

        adminUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminUser.setInitialPassword(false);
        adminUserRepository.save(adminUser);
    }

    // NEW HELPER METHOD: Generate a random N-digit numeric string
    private String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return sb.toString();
    }

    // UPDATED METHOD: Request password reset for Admin
    @Transactional
    public void requestPasswordReset(String email) throws MessagingException, IOException {
        AdminUser adminUser = adminUserRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));

        // Delete any existing tokens for this user to ensure only one valid token exists
        passwordResetTokenRepository.deleteByUserEmailAndUserType(email, UserType.ADMIN);

        // Generate a 6-digit numeric code
        String token = generateNumericCode(6); // Changed from UUID to 6-digit numeric code
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiryDate, UserType.ADMIN);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(email, adminUser.getUsername(), token, UserType.ADMIN);
    }

    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUserType(token, UserType.ADMIN)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired password reset token."));

        if (!resetToken.getUserEmail().equalsIgnoreCase(email)) {
            throw new BadCredentialsException("Token does not match provided email.");
        }
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BadCredentialsException("Password reset token has expired.");
        }

        AdminUser adminUser = adminUserRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));

        adminUser.setPassword(passwordEncoder.encode(newPassword));
        adminUser.setInitialPassword(false);
        adminUserRepository.save(adminUser);

        passwordResetTokenRepository.delete(resetToken);
    }
}