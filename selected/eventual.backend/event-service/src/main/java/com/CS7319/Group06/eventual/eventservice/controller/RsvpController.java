package com.CS7319.Group06.eventual.eventservice.controller;

import com.CS7319.Group06.eventual.eventservice.dao.RsvpDao;
import com.CS7319.Group06.eventual.eventservice.model.Rsvp;
import com.CS7319.Group06.eventual.eventservice.model.constants.RsvpStatus;
import com.CS7319.Group06.eventual.eventservice.service.RsvpService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * RSVP Controller — /api/events/{eventId}/rsvp
 */
@RestController
public class RsvpController {

    private final RsvpService rsvpService;
    private final RsvpDao rsvpDao;

    public RsvpController(RsvpService rsvpService, RsvpDao rsvpDao) {
        this.rsvpService = rsvpService;
        this.rsvpDao = rsvpDao;
    }

    /**
     * RSVP to an event. GOING or WAITLISTED based on event capacity.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/events/{eventId}/rsvp")
    public Rsvp rsvpToEvent(@PathVariable int eventId,
                            @RequestHeader(value = "X-Authenticated-User", required = false) String userEmail,
                            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        return rsvpService.rsvpToEvent(eventId, userEmail);
    }

    /**
     * Cancel the RSVP. The first WAITLISTED person is automatically promoted.
     */
    @DeleteMapping("/api/events/{eventId}/rsvp")
    public Rsvp cancelRsvp(@PathVariable int eventId,
                           @RequestHeader(value = "X-Authenticated-User", required = false) String userEmail,
                           @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        return rsvpService.cancelRsvp(eventId, userEmail);
    }

    /**
     * Get all RSVPs for an event — organizer only.
     */
    @GetMapping("/api/events/{eventId}/rsvp")
    public List<Rsvp> getRsvpsForEvent(@PathVariable int eventId,
                                       @RequestHeader(value = "X-Authenticated-User", required = false) String userEmail,
                                       @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return rsvpService.getRsvpsForEvent(eventId);
    }

    /**
     * Returns event IDs the given user has an active RSVP for (non-CANCELLED).
     * Called by the Search Service to exclude already-RSVPed events from recommendations.
     */
    @GetMapping("/api/events/rsvps/by-user/{email}")
    public List<Integer> getRsvpEventIdsByUser(@PathVariable String email) {
        return rsvpDao.getRsvpsByUser(email).stream()
                .filter(r -> r.getStatus() != RsvpStatus.CANCELLED)
                .map(Rsvp::getEventId)
                .toList();
    }
}
