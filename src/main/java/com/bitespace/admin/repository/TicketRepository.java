package com.bitespace.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bitespace.admin.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR :status = 'all' OR t.status = :status) AND " +
           "(:vendorName IS NULL OR LOWER(t.vendorName) LIKE LOWER(CONCAT('%', :vendorName, '%'))) AND " +
           "(:startDate IS NULL OR t.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR t.timestamp <= :endDate)")
    List<Ticket> searchAndFilterTickets(@Param("status") String status,
                                        @Param("vendorName") String vendorName,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    long countByStatus(String status);
}