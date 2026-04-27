package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.SupportTicket;

import java.util.List;

/**
 * Service interface for support ticket operations.
 */
public interface SupportTicketService {

    /**
     * Submit a new support ticket on behalf of the authenticated user.
     */
    SupportTicket submitTicket(SupportTicket ticket, String submittedByEmail);

    /**
     * Get a ticket by its ID.
     */
    SupportTicket getTicketById(int id, String requesterEmail, boolean isAdmin);

    /**
     * Get all tickets submitted by the authenticated user.
     */
    List<SupportTicket> getMyTickets(String email);

    /**
     * Get all tickets — admin only. Optionally filter by status (RESOLVED / UNRESOLVED).
     */
    List<SupportTicket> getAllTickets(String status);

    /**
     * Mark a ticket as RESOLVED or UNRESOLVED — admin only.
     */
    SupportTicket updateStatus(int id, String status);
}
