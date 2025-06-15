package com.bitespace.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class TicketDTO {
    private String id;
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer contact is required")
    private String customerContact;
    private String vendorName;

    @NotBlank(message = "Status is required")
    private String status;
    private LocalDateTime timestamp;
    @NotBlank(message = "Category is required")
    private String category;

    public TicketDTO() {
        // Default constructor
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }



    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}