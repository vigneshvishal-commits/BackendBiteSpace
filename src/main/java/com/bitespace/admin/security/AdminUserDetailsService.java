// src/main/java/com/bitespace/admin/security/AdminUserDetailsService.java
package com.bitespace.admin.security;

import com.bitespace.admin.model.AdminUser;
import com.bitespace.admin.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections; // To use Collections.singletonList for roles

@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with username: " + username));

        // Note: Spring Security expects roles to be prefixed with "ROLE_"
        return new org.springframework.security.core.userdetails.User(
                adminUser.getUsername(),
                adminUser.getPassword(),
                Collections.singletonList(() -> adminUser.getRole()) // Simple way to provide a single authority
        );
    }
}
