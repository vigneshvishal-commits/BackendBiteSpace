package com.bitespace.admin.controller;

import com.bitespace.admin.dto.AnalyticsResponse; // New import
import com.bitespace.admin.dto.DashboardSummaryResponse;
import com.bitespace.admin.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    // Endpoint for analytics data (charts)
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalyticsData() {
        AnalyticsResponse analyticsData = dashboardService.getAnalyticsData();
        return ResponseEntity.ok(analyticsData);
    }
}