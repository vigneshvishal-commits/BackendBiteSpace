package com.bitespace.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bitespace.admin.model.AdminUser;
import com.bitespace.admin.repository.AdminUserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminUserRepository.findByUsername("admin").isEmpty()) {
            AdminUser admin = new AdminUser();
            admin.setUsername("vickywizard2004@gmail.com");
            admin.setPassword(passwordEncoder.encode("adminpass")); // Initial password
            admin.setRole("ROLE_ADMIN");
            admin.setInitialPassword(true); // Explicitly set to true for new default admin
            adminUserRepository.save(admin);
            System.out.println("Default admin user created: admin/adminpass (initial password)");
        }
    }
}