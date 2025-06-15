package com.bitespace.admin.dto;

public class MonthlySalesOrdersData {
    private String month;
    private double sales;
    private long orders;

    public MonthlySalesOrdersData(String month, double sales, long orders) {
        this.month = month;
        this.sales = sales;
        this.orders = orders;
    }

    // Getters
    public String getMonth() {
        return month;
    }

    public double getSales() {
        return sales;
    }

    public long getOrders() {
        return orders;
    }

    // Setters (optional, typically not needed for a response DTO)
    public void setMonth(String month) {
        this.month = month;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }
}