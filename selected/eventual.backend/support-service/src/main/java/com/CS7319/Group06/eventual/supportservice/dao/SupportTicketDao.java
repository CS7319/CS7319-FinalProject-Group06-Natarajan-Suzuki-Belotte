package com.CS7319.Group06.eventual.supportservice.dao;

import com.CS7319.Group06.eventual.supportservice.model.SupportTicket;

import java.util.List;

/**
 * Data access operations for support tickets.
 */
public interface SupportTicketDao {

    SupportTicket createTicket(SupportTicket ticket);

    SupportTicket getTicketById(int id);

    List<SupportTicket> getTicketsByUser(String email);

    List<SupportTicket> getAllTickets(String status);

    SupportTicket updateStatus(int id, String status);
}
