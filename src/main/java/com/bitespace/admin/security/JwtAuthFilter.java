package com.bitespace.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // NEW IMPORT
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // For Vendors

    @Autowired
    private AdminUserDetailsService adminUserDetailsService; // For Admins (NEW AUTOWIRE)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                // Log and continue if token is invalid or expired
                logger.error("Invalid JWT token: " + e.getMessage());
            }
        }

        // If username is extracted from JWT and no authentication is set in SecurityContext yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            // --- Try loading user from AdminUserDetailsService first ---
            try {
                userDetails = adminUserDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException adminEx) {
                // If not found in admin users, try CustomUserDetailsService (for vendors)
                try {
                    userDetails = customUserDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException vendorEx) {
                    // User not found in either service, log a warning
                    logger.warn("User '" + username + "' not found in admin or vendor user details services.");
                }
            }

            // If userDetails are found and token is valid for these userDetails
            if (userDetails != null && jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}