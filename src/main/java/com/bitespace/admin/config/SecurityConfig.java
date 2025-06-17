package com.bitespace.admin.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bitespace.admin.security.AdminUserDetailsService; // Import for Admin users
import com.bitespace.admin.security.AuthEntryPoint;
import com.bitespace.admin.security.CustomUserDetailsService; // For Vendor users
import com.bitespace.admin.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // For Vendors

    @Autowired
    private AdminUserDetailsService adminUserDetailsService; // For Admins

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // These endpoints are publicly accessible (no authentication required)
                .requestMatchers(
                    "/api/admin/auth/login",
                    "/api/admin/auth/forgot-password",
                    "/api/admin/auth/reset-password",

                    "/api/vendor/auth/login",
                    "/api/vendor/auth/forgot-password",
                    "/api/vendor/auth/reset-password"
                ).permitAll()
                // All other /api/admin/** endpoints require authentication (Admin JWT)
                // This includes /api/admin/auth/change-password
                .requestMatchers("/api/admin/**").authenticated()
                // All other /api/vendor/** endpoints require authentication (Vendor JWT)
                // This includes /api/vendor/auth/change-password
                .requestMatchers("/api/vendor/**").authenticated()
                .anyRequest().authenticated() // Fallback for any other requests not explicitly matched
            );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Explicitly define an AuthenticationProvider for Vendors
    @Bean
    public DaoAuthenticationProvider vendorAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); // Assuming customUserDetailsService handles Vendors
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Explicitly define an AuthenticationProvider for Admins
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService); // Assuming adminUserDetailsService handles Admins
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Configure the AuthenticationManager to use both custom providers
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration,
            DaoAuthenticationProvider vendorAuthenticationProvider,
            DaoAuthenticationProvider adminAuthenticationProvider
    ) throws Exception {
        ProviderManager providerManager = new ProviderManager(Arrays.asList(
            vendorAuthenticationProvider,
            adminAuthenticationProvider
        ));
        providerManager.setEraseCredentialsAfterAuthentication(true);

        return providerManager;
    }
}