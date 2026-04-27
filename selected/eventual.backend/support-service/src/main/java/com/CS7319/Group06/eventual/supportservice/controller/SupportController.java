package com.CS7319.Group06.eventual.supportservice.controller;

import com.CS7319.Group06.eventual.supportservice.model.SupportTicket;
import com.CS7319.Group06.eventual.supportservice.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * REST controller for support ticket operations — submit tickets, view status, and resolve them.
 */
@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportTicketService supportTicketService;

    public SupportController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    // Submit a new support ticket
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SupportTicket submitTicket(@Valid @RequestBody SupportTicket ticket,
                                      @RequestHeader("X-Authenticated-User") String currentUser) {
        return supportTicketService.submitTicket(ticket, currentUser);
    }

    // Get the authenticated user's own tickets
    @GetMapping("/my")
    public List<SupportTicket> getMyTickets(@RequestHeader("X-Authenticated-User") String currentUser) {
        return supportTicketService.getMyTickets(currentUser);
    }

    // Get a specific ticket — user can only view their own; admin can view all
    @GetMapping("/{id}")
    public SupportTicket getTicketById(@PathVariable int id,
                                       @RequestHeader("X-Authenticated-User") String currentUser,
                                       @RequestHeader("X-User-Role") String role) {
        boolean isAdmin = "ADMIN".equals(role);
        return supportTicketService.getTicketById(id, currentUser, isAdmin);
    }

    // Get all tickets — ADMIN only, optional ?status=RESOLVED or ?status=UNRESOLVED filter
    @GetMapping
    public List<SupportTicket> getAllTickets(@RequestParam(required = false) String status,
                                             @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all tickets");
        }
        return supportTicketService.getAllTickets(status);
    }

    // Mark a ticket as RESOLVED or UNRESOLVED — ADMIN only
    // Body: { "status": "RESOLVED" }
    @PatchMapping("/{id}/status")
    public SupportTicket updateStatus(@PathVariable int id,
                                      @RequestBody Map<String, String> body,
                                      @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update ticket status");
        }
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        return supportTicketService.updateStatus(id, status);
    }
}
