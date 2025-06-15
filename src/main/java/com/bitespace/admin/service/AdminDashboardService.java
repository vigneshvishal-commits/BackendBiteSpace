package com.bitespace.admin.service;

import com.bitespace.admin.dto.AnalyticsResponse;
import com.bitespace.admin.dto.DashboardSummaryResponse;
import com.bitespace.admin.dto.MonthlySalesOrdersData;
import com.bitespace.admin.dto.VendorTypeDistributionData;
import com.bitespace.admin.dto.VendorLocationDistributionData;
import com.bitespace.admin.model.Vendor;
import com.bitespace.admin.repository.TicketRepository; // Still autowired but not currently used for dashboard
import com.bitespace.admin.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private TicketRepository ticketRepository;

    // Removed: @Autowired(required = false) private UserRepository userRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        long totalVendors = vendorRepository.count();
        long activeVendors = vendorRepository.countByIsActive(true);
        long inactiveVendors = vendorRepository.countByIsActive(false);

        // --- PLACEHOLDER VALUES FOR ORDERS AND REVENUE ---
        // To get actual totalOrders and totalRevenue, you would need to:
        // 1. Have an 'Order' entity with fields like 'totalAmount' and 'orderDate'.
        // 2. Have an 'OrderRepository' to query and sum these values.
        long totalOrders = 0; // Placeholder
        double totalRevenue = 0.0; // Placeholder

        // Removed: Logic for totalUsers

        return new DashboardSummaryResponse(totalVendors, activeVendors, inactiveVendors, totalOrders, totalRevenue);
    }

    public AnalyticsResponse getAnalyticsData() {
        // --- Graph 1: Monthly Sales & Orders ---
        // IMPORTANT: This data is currently MOCKED. To make it dynamic,
        // you will need 'Order' and 'Payment' entities, and logic to aggregate
        // sales (revenue) and order counts by month from your database.
        List<MonthlySalesOrdersData> monthlySalesAndOrders = new ArrayList<>();
        monthlySalesAndOrders.add(new MonthlySalesOrdersData("Jan", 4500.0, 250));
        monthlySalesAndOrders.add(new MonthlySalesOrdersData("Feb", 3800.0, 180));
        monthlySalesAndOrders.add(new MonthlySalesOrdersData("Mar", 5200.0, 300));
        monthlySalesAndOrders.add(new MonthlySalesOrdersData("Apr", 4100.0, 220));
        monthlySalesAndOrders.add(new MonthlySalesOrdersData("May", 5500.0, 320));
        monthlySalesAndOrders.add(new MonthlySalesOrdersData("Jun", 4800.0, 280));
        // Add more months as needed for a full year or dynamic range


        // --- Graph 2: Vendor Distribution by Type ---
        // This is dynamic and fetched from the VendorRepository
        Map<String, Long> vendorTypeCounts = vendorRepository.findAll().stream()
                .collect(Collectors.groupingBy(Vendor::getOutletType, Collectors.counting()));

        List<VendorTypeDistributionData> vendorDistributionByType = vendorTypeCounts.entrySet().stream()
                .map(entry -> new VendorTypeDistributionData(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // --- Graph 3: Vendor Distribution by Location ---
        // This is dynamic and fetched from the VendorRepository
        Map<String, Long> vendorLocationCounts = vendorRepository.findAll().stream()
                .collect(Collectors.groupingBy(Vendor::getLocation, Collectors.counting()));

        List<VendorLocationDistributionData> vendorDistributionByLocation = vendorLocationCounts.entrySet().stream()
                .map(entry -> new VendorLocationDistributionData(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());


        return new AnalyticsResponse(monthlySalesAndOrders, vendorDistributionByType, vendorDistributionByLocation);
    }
}