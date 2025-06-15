package com.bitespace.admin.dto;

public class DashboardSummaryResponse {
    private long totalVendors;
    private long activeVendors;
    private long inactiveVendors;
    private long totalOrders;
    private double totalRevenue;

    public DashboardSummaryResponse(long totalVendors, long activeVendors, long inactiveVendors, long totalOrders, double totalRevenue) {
        this.totalVendors = totalVendors;
        this.activeVendors = activeVendors;
        this.inactiveVendors = inactiveVendors;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public long getTotalVendors() {
        return totalVendors;
    }

    public long getActiveVendors() {
        return activeVendors;
    }

    public long getInactiveVendors() {
        return inactiveVendors;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    // Setters (if needed, though typically not for a response DTO)
    public void setTotalVendors(long totalVendors) {
        this.totalVendors = totalVendors;
    }

    public void setActiveVendors(long activeVendors) {
        this.activeVendors = activeVendors;
    }

    public void setInactiveVendors(long inactiveVendors) {
        this.inactiveVendors = inactiveVendors;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}