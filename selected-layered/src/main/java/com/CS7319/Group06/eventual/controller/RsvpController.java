package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.Rsvp;
import com.CS7319.Group06.eventual.service.RsvpService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RSVP Controller — /api/events/{eventId}/rsvp
 *
 * @author harininatarajan
 */
@RestController
@RequestMapping("/api/events/{eventId}/rsvp")
public class RsvpController {

    private final RsvpService rsvpService;

    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    /**
     * RSVP to an event. GOING or WAITLISTED based on event capacity.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Rsvp rsvpToEvent(@PathVariable int eventId, Authentication authentication) {
        return rsvpService.rsvpToEvent(eventId, authentication.getName());
    }

    /**
     * Cancel the RSVP. The first WAITLISTED person is automatically promoted.
     */
    @DeleteMapping
    public Rsvp cancelRsvp(@PathVariable int eventId, Authentication authentication) {
        return rsvpService.cancelRsvp(eventId, authentication.getName());
    }

    /**
     * Get all RSVPs for an event — organizer only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public List<Rsvp> getRsvpsForEvent(@PathVariable int eventId) {
        return rsvpService.getRsvpsForEvent(eventId);
    }
}
