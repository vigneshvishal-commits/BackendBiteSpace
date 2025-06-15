package com.bitespace.admin.dto;

import java.util.List;

public class AnalyticsResponse {
    private List<MonthlySalesOrdersData> monthlySalesAndOrders;
    private List<VendorTypeDistributionData> vendorDistributionByType;
    private List<VendorLocationDistributionData> vendorDistributionByLocation; // New field

    public AnalyticsResponse(List<MonthlySalesOrdersData> monthlySalesAndOrders, List<VendorTypeDistributionData> vendorDistributionByType, List<VendorLocationDistributionData> vendorDistributionByLocation) {
        this.monthlySalesAndOrders = monthlySalesAndOrders;
        this.vendorDistributionByType = vendorDistributionByType;
        this.vendorDistributionByLocation = vendorDistributionByLocation;
    }

    // Getters
    public List<MonthlySalesOrdersData> getMonthlySalesAndOrders() {
        return monthlySalesAndOrders;
    }

    public List<VendorTypeDistributionData> getVendorDistributionByType() {
        return vendorDistributionByType;
    }

    public List<VendorLocationDistributionData> getVendorDistributionByLocation() {
        return vendorDistributionByLocation;
    }

    // Setters (optional, typically not needed for a response DTO)
    public void setMonthlySalesAndOrders(List<MonthlySalesOrdersData> monthlySalesAndOrders) {
        this.monthlySalesAndOrders = monthlySalesAndOrders;
    }

    public void setVendorDistributionByType(List<VendorTypeDistributionData> vendorDistributionByType) {
        this.vendorDistributionByType = vendorDistributionByType;
    }

    public void setVendorDistributionByLocation(List<VendorLocationDistributionData> vendorDistributionByLocation) {
        this.vendorDistributionByLocation = vendorDistributionByLocation;
    }
}