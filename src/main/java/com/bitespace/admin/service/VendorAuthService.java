package com.bitespace.admin.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitespace.admin.dto.AuthResponse;
import com.bitespace.admin.dto.VendorChangePasswordRequest;
import com.bitespace.admin.dto.VendorLoginRequest;
import com.bitespace.admin.exception.ResourceNotFoundException;
import com.bitespace.admin.model.PasswordResetToken;
import com.bitespace.admin.model.Vendor;
import com.bitespace.admin.repository.PasswordResetTokenRepository;
import com.bitespace.admin.repository.VendorRepository;
import com.bitespace.admin.security.JwtUtil;
import com.bitespace.common.model.UserType;

import jakarta.mail.MessagingException;

@Service
public class VendorAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthResponse loginVendor(VendorLoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            Optional<Vendor> optionalVendor = vendorRepository.findByVendorEmail(loginRequest.getEmail());
            if (!optionalVendor.isPresent()) {
                throw new ResourceNotFoundException("Vendor not found after authentication.");
            }
            Vendor vendor = optionalVendor.get();

            // --- CRITICAL CHANGE HERE ---
            // Pass userDetails.getUsername() (which is the email in this case) as the username
            return new AuthResponse(jwt, vendor.getVendorEmail(), vendor.getIsInitialPassword(), userDetails.getUsername());

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password.");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

    public AuthResponse changePassword(String vendorEmail, VendorChangePasswordRequest changePasswordRequest) {
        Optional<Vendor> optionalVendor = vendorRepository.findByVendorEmail(vendorEmail);
        if (!optionalVendor.isPresent()) {
            throw new ResourceNotFoundException("Vendor not found with email: " + vendorEmail);
        }
        Vendor vendor = optionalVendor.get();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), vendor.getPassword())) {
            throw new BadCredentialsException("Incorrect old password.");
        }

        vendor.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        vendor.setIsInitialPassword(false);
        vendorRepository.save(vendor);

        String newJwt = jwtUtil.generateToken(vendor.getVendorEmail());
        // Ensure this AuthResponse constructor also matches the updated DTO if it's used elsewhere
        // You might want to pass vendor.getVendorEmail() as the username here too
        return new AuthResponse(newJwt, vendor.getVendorEmail(), vendor.getIsInitialPassword(), vendor.getVendorEmail());
    }

    private String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // UPDATED METHOD: Request password reset for Vendor
    @Transactional
    public void requestPasswordReset(String email) throws MessagingException, IOException {
        Vendor vendor = vendorRepository.findByVendorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with email: " + email));

        passwordResetTokenRepository.deleteByUserEmailAndUserType(email, UserType.VENDOR);

        String token = generateNumericCode(6);
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiryDate, UserType.VENDOR);
        passwordResetTokenRepository.save(resetToken);

        // --- IMPORTANT CHANGE HERE ---
        // Provide a default name if vendor.getVendorName() is null or empty
        String nameForEmail = (vendor.getVendorName() != null && !vendor.getVendorName().trim().isEmpty())
                              ? vendor.getVendorName()
                              : "Vendor User"; // Default name if null or empty

        emailService.sendPasswordResetEmail(email, nameForEmail, token, UserType.VENDOR);
    }

    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUserType(token, UserType.VENDOR)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired password reset token."));

        if (!resetToken.getUserEmail().equalsIgnoreCase(email)) {
            throw new BadCredentialsException("Token does not match provided email.");
        }
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BadCredentialsException("Password reset token has expired.");
        }

        Vendor vendor = vendorRepository.findByVendorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with email: " + email));

        vendor.setPassword(passwordEncoder.encode(newPassword));
        vendor.setIsInitialPassword(false);
        vendorRepository.save(vendor);

        passwordResetTokenRepository.delete(resetToken);
    }
}