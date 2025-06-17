package com.bitespace.admin.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendors") // Assuming your table name is 'vendors'
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String outletName;
    private String vendorName;
    private String vendorEmail;
    private String location;
    private String contact;
    private String outletType;
    private Boolean isActive;
    private LocalDate joinDate;

    private String password; // New: Hashed password
    private Boolean isInitialPassword; // New: Flag for first-time login password change

    public Vendor() {
    }

    public Vendor(Long id, String outletName, String vendorName, String vendorEmail, String location, String contact, String outletType, Boolean isActive, LocalDate joinDate, String password, Boolean isInitialPassword) {
        this.id = id;
        this.outletName = outletName;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.location = location;
        this.contact = contact;
        this.outletType = outletType;
        this.isActive = isActive;
        this.joinDate = joinDate;
        this.password = password;
        this.isInitialPassword = isInitialPassword;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getOutletName() {
        return outletName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getOutletType() {
        return outletType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getIsInitialPassword() {
        return isInitialPassword;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setOutletType(String outletType) {
        this.outletType = outletType;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsInitialPassword(Boolean initialPassword) {
        isInitialPassword = initialPassword;
    }
}