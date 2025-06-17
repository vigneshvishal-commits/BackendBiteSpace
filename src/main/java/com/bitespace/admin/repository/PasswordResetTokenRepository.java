package com.bitespace.admin.repository; // Or com.bitespace.common.repository

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bitespace.admin.model.PasswordResetToken;
import com.bitespace.common.model.UserType;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUserType(String token, UserType userType);
    Optional<PasswordResetToken> findByUserEmailAndUserType(String userEmail, UserType userType);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.userEmail = ?1 AND t.userType = ?2")
    void deleteByUserEmailAndUserType(String userEmail, UserType userType);
}