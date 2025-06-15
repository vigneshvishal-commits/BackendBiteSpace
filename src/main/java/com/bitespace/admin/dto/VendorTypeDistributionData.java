package com.bitespace.admin.dto;

public class VendorTypeDistributionData {
    private String name;  // e.g., "Fast Food"
    private long value;   // Count of vendors

    public VendorTypeDistributionData(String name, long value) {
        this.name = name;
        this.value = value;
    }

    // Getters
    public String getName() {
        return name;
    }

    public long getValue() {
        return value;
    }

    // Setters (optional, typically not needed for a response DTO)
    public void setName(String name) {
        this.name = name;
    }

    public void setValue(long value) {
        this.value = value;
    }
}