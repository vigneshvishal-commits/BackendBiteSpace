package com.bitespace.admin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // e.g., "ROLE_ADMIN"

    @Column(nullable = false) // NEW FIELD
    private boolean isInitialPassword = true; // Default to true for new admins

    public AdminUser() {
    }

    // You might have other constructors. Ensure they handle isInitialPassword.
    // Example with all fields:
    public AdminUser(Long id, String username, String password, String role, boolean isInitialPassword) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isInitialPassword = isInitialPassword;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isInitialPassword() { // Getter for isInitialPassword
        return isInitialPassword;
    }

    public void setInitialPassword(boolean initialPassword) { // Setter for isInitialPassword
        isInitialPassword = initialPassword;
    }
}