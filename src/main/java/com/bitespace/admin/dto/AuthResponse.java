package com.bitespace.admin.dto;

public class AuthResponse {
    private String jwt;
    private String email;
    private Boolean isInitialPassword; // To indicate if password change is required

    public AuthResponse() {
    }

    public AuthResponse(String jwt, String email, Boolean isInitialPassword) {
        this.jwt = jwt;
        this.email = email;
        this.isInitialPassword = isInitialPassword;
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
}