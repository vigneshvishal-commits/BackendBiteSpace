package com.bitespace.admin.service;

import com.bitespace.admin.dto.TicketCountsResponse;
import com.bitespace.admin.dto.TicketDTO;
import com.bitespace.admin.exception.ResourceNotFoundException;
import com.bitespace.admin.model.Ticket;
import com.bitespace.admin.repository.TicketRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminTicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<TicketDTO> getAllTickets(String status, String dateFilter, String vendorName) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (dateFilter != null && !dateFilter.equalsIgnoreCase("all")) {
            LocalDateTime now = LocalDateTime.now();
            switch (dateFilter.toLowerCase()) {
                case "today":
                    startDate = now.truncatedTo(ChronoUnit.DAYS);
                    endDate = startDate.plusDays(1).minusNanos(1);
                    break;
                case "week":
                    startDate = now.with(java.time.DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
                    endDate = startDate.plusWeeks(1).minusNanos(1);
                    break;
                case "month":
                    startDate = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                    endDate = startDate.plusMonths(1).minusNanos(1);
                    break;
            }
        }
        List<Ticket> tickets = ticketRepository.searchAndFilterTickets(status, vendorName, startDate, endDate);
        return tickets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TicketDTO updateTicketStatus(String id, String newStatus) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        existingTicket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return convertToDto(updatedTicket);
    }

    public TicketCountsResponse getTicketCounts() {
        long open = ticketRepository.countByStatus("open");
        long inProgress = ticketRepository.countByStatus("in-progress");
        long resolved = ticketRepository.countByStatus("resolved");
        return new TicketCountsResponse(open, inProgress, resolved);
    }

    private TicketDTO convertToDto(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        BeanUtils.copyProperties(ticket, ticketDTO);
        return ticketDTO;
    }

    // Method to create a ticket (for seeding/testing, usually from User module)
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(ticketDTO, ticket);
        if (ticket.getId() == null || ticket.getId().isEmpty()) {
            // Generate a simple ID, in real app use sequence/UUID
            ticket.setId("TKT" + String.format("%03d", (ticketRepository.count() + 1)));
        }
        if (ticket.getTimestamp() == null) {
            ticket.setTimestamp(LocalDateTime.now());
        }
        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDto(savedTicket);
    }
}