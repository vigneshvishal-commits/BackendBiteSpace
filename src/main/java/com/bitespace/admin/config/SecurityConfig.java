package com.bitespace.admin.config;

import com.bitespace.admin.security.AdminUserDetailsService; // Import for Admin users
import com.bitespace.admin.security.AuthEntryPoint;
import com.bitespace.admin.security.CustomUserDetailsService; // For Vendor users
import com.bitespace.admin.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
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

import java.util.Arrays;

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
                // Allow public access to authentication endpoints for both admin and vendor
                // ADDED FORGOT/RESET PASSWORD ENDPOINTS HERE:
                .requestMatchers(
                    "/api/admin/auth/login",
                    "/api/admin/auth/change-password", // Change password requires JWT, but needs to be accessible *if initial password*
                    "/api/admin/auth/forgot-password", // NEW: Publicly accessible
                    "/api/admin/auth/reset-password",  // NEW: Publicly accessible

                    "/api/vendor/auth/login",
                    "/api/vendor/auth/change-password", // Change password requires JWT, but needs to be accessible *if initial password*
                    "/api/vendor/auth/forgot-password", // NEW: Publicly accessible
                    "/api/vendor/auth/reset-password"   // NEW: Publicly accessible
                ).permitAll()
                // All other /api/admin/** endpoints require authentication (Admin JWT)
                .requestMatchers("/api/admin/**").authenticated()
                // All other /api/vendor/** endpoints require authentication (Vendor JWT)
                .requestMatchers("/api/vendor/**").authenticated()
                .anyRequest().authenticated() // All other requests still require authentication
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
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Explicitly define an AuthenticationProvider for Admins
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
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