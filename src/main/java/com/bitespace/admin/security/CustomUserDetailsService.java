package com.bitespace.admin.security;

import com.bitespace.admin.model.Vendor;
import com.bitespace.admin.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Vendor vendor = vendorRepository.findByVendorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Vendor not found with email: " + email));

        // Note: For simplicity, we are granting a generic "VENDOR" role.
        // In a real application, you might have more granular roles or authorities.
        return new org.springframework.security.core.userdetails.User(vendor.getVendorEmail(), vendor.getPassword(), new ArrayList<>());
    }
}