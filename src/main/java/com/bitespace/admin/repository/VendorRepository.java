package com.bitespace.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitespace.admin.model.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    long countByIsActive(boolean isActive);

    List<Vendor> findByOutletNameContainingIgnoreCase(String outletName);

    // New: Find a vendor by email
    Optional<Vendor> findByVendorEmail(String vendorEmail);

}