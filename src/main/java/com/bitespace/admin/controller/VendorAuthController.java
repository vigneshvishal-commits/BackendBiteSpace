package com.bitespace.admin.controller; // Or com.bitespace.vendor.controller

import com.bitespace.admin.dto.AuthResponse;
import com.bitespace.admin.dto.VendorChangePasswordRequest;
import com.bitespace.admin.dto.VendorLoginRequest;
import com.bitespace.admin.exception.ResourceNotFoundException;
import com.bitespace.admin.dto.ForgotPasswordRequest; // NEW IMPORT
import com.bitespace.admin.dto.ResetPasswordRequest; // NEW IMPORT
import com.bitespace.admin.service.VendorAuthService;
import jakarta.mail.MessagingException; // NEW IMPORT
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException; // NEW IMPORT

@RestController
@RequestMapping("/api/vendor/auth")
public class VendorAuthController {

    @Autowired
    private VendorAuthService vendorAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginVendor(@Valid @RequestBody VendorLoginRequest loginRequest) {
        AuthResponse response = vendorAuthService.loginVendor(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody VendorChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String vendorEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        AuthResponse response = vendorAuthService.changePassword(vendorEmail, changePasswordRequest);
        return ResponseEntity.ok(response);
    }

    // NEW ENDPOINT: Request password reset for Vendor
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestVendorPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            vendorAuthService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok("If an account with that email exists, a password reset code has been sent.");
        } catch (ResourceNotFoundException e) {
            // Return a generic success message even if email not found to prevent user enumeration
            return ResponseEntity.ok("If an account with that email exists, a password reset code has been sent.");
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: Reset password for Vendor
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetVendorPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            vendorAuthService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Vendor password has been reset successfully.");
        } catch (BadCredentialsException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}