package com.bitespace.admin.service;

import com.bitespace.admin.dto.CredentialRequest;
import com.bitespace.admin.dto.VendorDTO;
import com.bitespace.admin.exception.ResourceNotFoundException;
import com.bitespace.admin.model.Vendor;
import com.bitespace.admin.repository.VendorRepository;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // New import
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminVendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder; // New Autowired

    public List<VendorDTO> getAllVendors(String search, String status, String type, String location) {
        List<Vendor> vendors;

        if (search != null && !search.isEmpty()) {
            vendors = vendorRepository.findByOutletNameContainingIgnoreCase(search);
        } else {
            vendors = vendorRepository.findAll();
        }

        // Apply filters
        return vendors.stream()
                .filter(vendor -> {
                    boolean statusMatch = true;
                    if (status != null && !status.equalsIgnoreCase("all")) {
                        boolean isActiveStatus = status.equalsIgnoreCase("active");
                        statusMatch = vendor.getIsActive() == isActiveStatus;
                    }
                    return statusMatch;
                })
                .filter(vendor -> {
                    boolean typeMatch = true;
                    if (type != null && !type.equalsIgnoreCase("all")) {
                        typeMatch = vendor.getOutletType().equalsIgnoreCase(type);
                    }
                    return typeMatch;
                })
                .filter(vendor -> {
                    boolean locationMatch = true;
                    if (location != null && !location.equalsIgnoreCase("all")) {
                        locationMatch = vendor.getLocation().equalsIgnoreCase(location);
                    }
                    return locationMatch;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VendorDTO createVendor(VendorDTO vendorDTO) {
        Vendor vendor = convertToEntity(vendorDTO);
        vendor.setIsActive(true); // New vendors are active by default
        vendor.setJoinDate(LocalDate.now()); // Set join date
        // Password and isInitialPassword will be set during credential generation
        vendor.setPassword(null); // Ensure password is null initially
        vendor.setIsInitialPassword(true); // Assume initial password is true until generated
        Vendor savedVendor = vendorRepository.save(vendor);
        return convertToDTO(savedVendor);
    }

    public VendorDTO updateVendor(Long id, VendorDTO vendorDTO) {
        Vendor existingVendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        existingVendor.setOutletName(vendorDTO.getOutletName());
        existingVendor.setVendorName(vendorDTO.getVendorName());
        existingVendor.setVendorEmail(vendorDTO.getVendorEmail());
        existingVendor.setLocation(vendorDTO.getLocation());
        existingVendor.setContact(vendorDTO.getContact());
        existingVendor.setOutletType(vendorDTO.getOutletType());
        // isActive and joinDate are not directly updated via this DTO usually
        // existingVendor.setIsActive(vendorDTO.getIsActive()); // If you want to allow updating active status here
        // existingVendor.setJoinDate(vendorDTO.getJoinDate()); // If you want to allow updating join date here

        Vendor updatedVendor = vendorRepository.save(existingVendor);
        return convertToDTO(updatedVendor);
    }

    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }

    public VendorDTO toggleVendorStatus(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendor.setIsActive(!vendor.getIsActive());
        Vendor updatedVendor = vendorRepository.save(vendor);
        return convertToDTO(updatedVendor);
    }

    public void generateAndSendCredentials(Long id, CredentialRequest credentialRequest) throws MessagingException, IOException {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        String generatedPassword = UUID.randomUUID().toString().substring(0, 8); // Simple random password
        String hashedPassword = passwordEncoder.encode(generatedPassword); // Hash the password

        vendor.setPassword(hashedPassword);
        vendor.setIsInitialPassword(true); // Set to true for forced password change on first login
        vendorRepository.save(vendor);

        // Corrected call: Pass vendor's email as username and the generated password
        emailService.sendVendorCredentialsEmail(vendor.getVendorEmail(), vendor.getVendorName(), vendor.getVendorEmail(), generatedPassword);
    }

    private VendorDTO convertToDTO(Vendor vendor) {
        VendorDTO dto = new VendorDTO();
        dto.setId(vendor.getId());
        dto.setOutletName(vendor.getOutletName());
        dto.setVendorName(vendor.getVendorName());
        dto.setVendorEmail(vendor.getVendorEmail());
        dto.setLocation(vendor.getLocation());
        dto.setContact(vendor.getContact());
        dto.setOutletType(vendor.getOutletType());
        dto.setIsActive(vendor.getIsActive());
        dto.setJoinDate(vendor.getJoinDate());
        return dto;
    }

    private Vendor convertToEntity(VendorDTO dto) {
        Vendor vendor = new Vendor();
        vendor.setId(dto.getId()); // ID might be null for new vendors
        vendor.setOutletName(dto.getOutletName());
        vendor.setVendorName(dto.getVendorName());
        vendor.setVendorEmail(dto.getVendorEmail());
        vendor.setLocation(dto.getLocation());
        vendor.setContact(dto.getContact());
        vendor.setOutletType(dto.getOutletType());
        vendor.setIsActive(dto.getIsActive());
        vendor.setJoinDate(dto.getJoinDate());
        return vendor;
    }
}