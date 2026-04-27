package com.CS7319.Group06.eventual.supportservice.service;

import com.CS7319.Group06.eventual.supportservice.model.SupportTicket;

import java.util.List;

/**
 * Service interface for support ticket operations.
 */
public interface SupportTicketService {

    SupportTicket submitTicket(SupportTicket ticket, String submittedByEmail);

    SupportTicket getTicketById(int id, String requesterEmail, boolean isAdmin);

    List<SupportTicket> getMyTickets(String email);

    List<SupportTicket> getAllTickets(String status);

    SupportTicket updateStatus(int id, String status);
}
