package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.SupportTicket;
import com.CS7319.Group06.eventual.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
                                      Authentication authentication) {
        return supportTicketService.submitTicket(ticket, authentication.getName());
    }

    // Get the authenticated user's own tickets
    @GetMapping("/my")
    public List<SupportTicket> getMyTickets(Authentication authentication) {
        return supportTicketService.getMyTickets(authentication.getName());
    }

    // Get a specific ticket — user can only view their own; admin can view all
    @GetMapping("/{id}")
    public SupportTicket getTicketById(@PathVariable int id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return supportTicketService.getTicketById(id, authentication.getName(), isAdmin);
    }

    // Get all tickets — admin only, optional ?status=RESOLVED or ?status=UNRESOLVED filter
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SupportTicket> getAllTickets(@RequestParam(required = false) String status) {
        return supportTicketService.getAllTickets(status);
    }

    // Mark a ticket as RESOLVED or UNRESOLVED — admin only
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public SupportTicket updateStatus(@PathVariable int id,
                                      @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "status is required");
        }
        return supportTicketService.updateStatus(id, status);
    }
}
