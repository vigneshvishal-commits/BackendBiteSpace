package com.bitespace.admin.dto;

public class AuthResponse {
    private String jwt;
    private String email;
    private Boolean isInitialPassword; // To indicate if password change is required
    private String username; // Added this field

    public AuthResponse() {
    }

    public AuthResponse(String jwt, String email, Boolean isInitialPassword, String username) { // Updated constructor
        this.jwt = jwt;
        this.email = email;
        this.isInitialPassword = isInitialPassword;
        this.username = username; // Set the username
    }

    // Getters
    public String getJwt() {
        return jwt;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsInitialPassword() {
        return isInitialPassword;
    }

    public String getUsername() { // Added getter for username
        return username;
    }

    // Setters
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsInitialPassword(Boolean initialPassword) {
        isInitialPassword = initialPassword;
    }

    public void setUsername(String username) { // Added setter for username
        this.username = username;
    }
}