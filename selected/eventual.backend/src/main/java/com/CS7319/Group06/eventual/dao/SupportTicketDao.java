package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.SupportTicket;

import java.util.List;

/**
 * Data access operations for support tickets.
 */
public interface SupportTicketDao {

    /**
     * Submit a new support ticket.
     */
    SupportTicket createTicket(SupportTicket ticket);

    /**
     * Get a single ticket by its ID.
     */
    SupportTicket getTicketById(int id);

    /**
     * Get all tickets submitted by a specific user.
     */
    List<SupportTicket> getTicketsByUser(String email);

    /**
     * Get all tickets (admin view), optionally filtered by status.
     * Pass null to return all tickets regardless of status.
     */
    List<SupportTicket> getAllTickets(String status);

    /**
     * Mark a ticket as resolved or unresolved.
     */
    SupportTicket updateStatus(int id, String status);
}
