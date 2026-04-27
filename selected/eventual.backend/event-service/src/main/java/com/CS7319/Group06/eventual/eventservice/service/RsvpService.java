package com.CS7319.Group06.eventual.eventservice.service;

import com.CS7319.Group06.eventual.eventservice.model.Rsvp;

import java.util.List;

/**
 * Service interface for RSVP operations
 */
public interface RsvpService {

    /**
     * RSVP the user to an event. Automatically assigns GOING or WAITLISTED based on capacity.
     *
     * @param eventId
     * @param userEmail
     * @return
     */
    Rsvp rsvpToEvent(int eventId, String userEmail);

    /**
     * Cancel the users RSVP. Automatically promotes the first waitlisted user if a GOING spot opens up.
     *
     * @param eventId
     * @param userEmail
     * @return
     */
    Rsvp cancelRsvp(int eventId, String userEmail);

    /**
     * Get all RSVPs for an event (only for organizer)
     *
     * @param eventId
     * @return
     */
    List<Rsvp> getRsvpsForEvent(int eventId);
}
