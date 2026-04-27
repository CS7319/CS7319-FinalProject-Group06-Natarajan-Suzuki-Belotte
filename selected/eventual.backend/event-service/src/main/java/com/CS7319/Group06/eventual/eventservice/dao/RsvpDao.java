package com.CS7319.Group06.eventual.eventservice.dao;

import com.CS7319.Group06.eventual.eventservice.model.Rsvp;
import com.CS7319.Group06.eventual.eventservice.model.constants.RsvpStatus;

import java.util.List;

/**
 * Data layer for RSVPs
 */
public interface RsvpDao {

    /**
     * Get rsvp for a particular event and user
     *
     * @param eventId
     * @param userEmail
     * @return
     */
    Rsvp getRsvpByEventAndUser(int eventId, String userEmail);

    /**
     * Get all rsvps for the event
     *
     * @param eventId
     * @return
     */
    List<Rsvp> getRsvpsByEvent(int eventId);

    /**
     * Create a new rsvp
     *
     * @param rsvp
     * @return
     */
    Rsvp createRsvp(Rsvp rsvp);

    /**
     * Update rsvp status
     *
     * @param eventId
     * @param userEmail
     * @param status
     * @return
     */
    Rsvp updateRsvpStatus(int eventId, String userEmail, RsvpStatus status);

    /**
     * Count of users with GOING status for an event
     *
     * @param eventId
     * @return
     */
    int countGoingByEvent(int eventId);

    /**
     * Get the first waitlisted rsvp for an event (earliest by created_at)
     *
     * @param eventId
     * @return
     */
    Rsvp getFirstWaitlisted(int eventId);

    /**
     * Get all RSVPs made by a user
     *
     * @param userEmail
     * @return
     */
    List<Rsvp> getRsvpsByUser(String userEmail);
}
