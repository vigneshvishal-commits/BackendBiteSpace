package com.bitespace.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitespace.admin.dto.TicketCountsResponse;
import com.bitespace.admin.dto.TicketDTO;
import com.bitespace.admin.service.AdminTicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminTicketController {

    @Autowired
    private AdminTicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "date") String dateFilter,
            @RequestParam(required = false) String vendor) {
        List<TicketDTO> tickets = ticketService.getAllTickets(status, dateFilter, vendor);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketDTO> updateTicketStatus(@PathVariable String id, @RequestParam String newStatus) {
        TicketDTO updatedTicket = ticketService.updateTicketStatus(id, newStatus);
        return ResponseEntity.ok(updatedTicket);
    }

    @GetMapping("/counts")
    public ResponseEntity<TicketCountsResponse> getTicketCounts() {
        TicketCountsResponse counts = ticketService.getTicketCounts();
        return ResponseEntity.ok(counts);
    }

    // Endpoint to create a ticket (for testing purposes using postman) - typically done by User module 
    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketDTO ticketDTO) {
        TicketDTO createdTicket = ticketService.createTicket(ticketDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }
}