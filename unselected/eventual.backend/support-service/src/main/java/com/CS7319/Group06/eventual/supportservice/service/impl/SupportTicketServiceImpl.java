package com.CS7319.Group06.eventual.supportservice.service.impl;

import com.CS7319.Group06.eventual.supportservice.dao.SupportTicketDao;
import com.CS7319.Group06.eventual.supportservice.exception.DaoException;
import com.CS7319.Group06.eventual.supportservice.model.SupportTicket;
import com.CS7319.Group06.eventual.supportservice.model.constants.TicketStatus;
import com.CS7319.Group06.eventual.supportservice.service.SupportTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implementation of SupportTicketService.
 */
@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketDao supportTicketDao;

    public SupportTicketServiceImpl(SupportTicketDao supportTicketDao) {
        this.supportTicketDao = supportTicketDao;
    }

    @Override
    public SupportTicket submitTicket(SupportTicket ticket, String submittedByEmail) {
        ticket.setSubmittedBy(submittedByEmail);
        try {
            return supportTicketDao.createTicket(ticket);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public SupportTicket getTicketById(int id, String requesterEmail, boolean isAdmin) {
        try {
            SupportTicket ticket = supportTicketDao.getTicketById(id);
            if (ticket == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found with id: " + id);
            if (!isAdmin && !ticket.getSubmittedBy().equals(requesterEmail))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            return ticket;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<SupportTicket> getMyTickets(String email) {
        try {
            return supportTicketDao.getTicketsByUser(email);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<SupportTicket> getAllTickets(String status) {
        if (status != null) {
            try {
                TicketStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid status. Must be RESOLVED or UNRESOLVED");
            }
        }
        try {
            return supportTicketDao.getAllTickets(status);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public SupportTicket updateStatus(int id, String status) {
        try {
            TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status. Must be RESOLVED or UNRESOLVED");
        }
        try {
            SupportTicket ticket = supportTicketDao.getTicketById(id);
            if (ticket == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found with id: " + id);
            return supportTicketDao.updateStatus(id, status.toUpperCase());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
