package com.bitespace.admin.controller;

import com.bitespace.admin.dto.CredentialRequest;
import com.bitespace.admin.dto.VendorDTO;
import com.bitespace.admin.service.AdminVendorService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/vendors")
public class AdminVendorController {

    @Autowired
    private AdminVendorService adminVendorService;

    @GetMapping
    public ResponseEntity<List<VendorDTO>> getAllVendors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location) {
        List<VendorDTO> vendors = adminVendorService.getAllVendors(search, status, type, location);
        return ResponseEntity.ok(vendors);
    }

    @PostMapping
    public ResponseEntity<VendorDTO> createVendor(@Valid @RequestBody VendorDTO vendorDTO) {
        VendorDTO createdVendor = adminVendorService.createVendor(vendorDTO);
        return new ResponseEntity<>(createdVendor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorDTO> updateVendor(@PathVariable Long id, @Valid @RequestBody VendorDTO vendorDTO) {
        VendorDTO updatedVendor = adminVendorService.updateVendor(id, vendorDTO);
        return ResponseEntity.ok(updatedVendor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        adminVendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<VendorDTO> toggleVendorStatus(@PathVariable Long id) {
        VendorDTO updatedVendor = adminVendorService.toggleVendorStatus(id);
        return ResponseEntity.ok(updatedVendor);
    }

    @PostMapping("/{id}/credentials")
    public ResponseEntity<String> generateAndSendCredentials(@PathVariable Long id, @RequestBody CredentialRequest credentialRequest) throws MessagingException, IOException {
        adminVendorService.generateAndSendCredentials(id, credentialRequest);
        return ResponseEntity.ok("Credentials generated and email sent successfully.");
    }
}