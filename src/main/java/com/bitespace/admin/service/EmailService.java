package com.bitespace.admin.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.bitespace.common.model.UserType; // NEW IMPORT

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVendorCredentialsEmail(String toEmail, String vendorName, String username, String password) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("vigneshvishal2207@gmail.com"); // Your configured email
        helper.setTo(toEmail);
        helper.setSubject("Bites Space: Your New Vendor Account Credentials");

        String htmlTemplate;
        try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("email-template.html"), StandardCharsets.UTF_8)) {
            htmlTemplate = FileCopyUtils.copyToString(reader);
        }

        htmlTemplate = htmlTemplate.replace("${vendorName}", vendorName);
        htmlTemplate = htmlTemplate.replace("${username}", username);
        htmlTemplate = htmlTemplate.replace("${password}", password);

        helper.setText(htmlTemplate, true);
        mailSender.send(message);
    }

    // NEW METHOD FOR PASSWORD RESET
    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken, UserType userType) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("vigneshvishal2207@gmail.com"); // Your configured email
        helper.setTo(toEmail);
        helper.setSubject("Bites Space: Password Reset Request");

        String templateFileName = "";
        if (userType == UserType.ADMIN) {
            templateFileName = "admin-password-reset-email.html"; // Create this new template
        } else if (userType == UserType.VENDOR) {
            templateFileName = "vendor-password-reset-email.html"; // Create this new template
        } else {
            throw new IllegalArgumentException("Unsupported UserType for password reset email.");
        }

        String htmlTemplate;
        try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(templateFileName), StandardCharsets.UTF_8)) {
            htmlTemplate = FileCopyUtils.copyToString(reader);
        }

        htmlTemplate = htmlTemplate.replace("${userName}", userName);
        htmlTemplate = htmlTemplate.replace("${resetToken}", resetToken);
        // You might want to include a full reset link for the frontend:
        // htmlTemplate = htmlTemplate.replace("${resetLink}", "http://yourfrontend.com/reset-password?token=" + resetToken + "&email=" + toEmail + "&userType=" + userType.name().toLowerCase());

        helper.setText(htmlTemplate, true);
        mailSender.send(message);
    }
}