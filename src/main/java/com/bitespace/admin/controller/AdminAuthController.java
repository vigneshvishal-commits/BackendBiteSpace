package com.bitespace.admin.controller;

import java.io.IOException; // NEW IMPORT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // NEW IMPORT
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitespace.admin.dto.AdminChangePasswordRequest;
import com.bitespace.admin.dto.AdminLoginRequest;
import com.bitespace.admin.dto.AuthResponse;
import com.bitespace.admin.dto.ForgotPasswordRequest; // NEW IMPORT
import com.bitespace.admin.dto.ResetPasswordRequest; // NEW IMPORT
import com.bitespace.admin.service.AdminAuthService;

import jakarta.mail.MessagingException; // NEW IMPORT
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody AdminLoginRequest loginRequest) {
        AuthResponse response = adminAuthService.loginAdmin(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changeAdminPassword(@Valid @RequestBody AdminChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        adminAuthService.changeAdminPassword(username, request);
        return ResponseEntity.ok("Admin password changed successfully!");
    }

    // NEW ENDPOINT: Request password reset for Admin
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestAdminPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            adminAuthService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok("If an account with that email exists, a password reset code has been sent.");
        } catch (UsernameNotFoundException e) {
            // Return a generic success message even if email not found to prevent user enumeration
            return ResponseEntity.ok("If an account with that email exists, a password reset code has been sent.");
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: Reset password for Admin
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetAdminPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            adminAuthService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Admin password has been reset successfully.");
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}