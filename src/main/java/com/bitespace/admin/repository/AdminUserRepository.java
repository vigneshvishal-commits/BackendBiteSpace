// src/main/java/com/bitespace/admin/repository/AdminUserRepository.java
package com.bitespace.admin.repository;

import com.bitespace.admin.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
}