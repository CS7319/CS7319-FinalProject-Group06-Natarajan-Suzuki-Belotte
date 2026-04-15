package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Rsvp;
import com.CS7319.Group06.eventual.model.constants.RsvpStatus;

import java.util.List;

/**
 * Data layer for RSVPs
 *
 * @author harininatarajan
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
     * Get all rsvp for the event
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
     * Update rsvp
     *
     * @param eventId
     * @param userEmail
     * @param status
     * @return
     */
    Rsvp updateRsvpStatus(int eventId, String userEmail, RsvpStatus status);

    /**
     * Count of users attending an event
     *
     * @param eventId
     * @return
     */
    int countGoingByEvent(int eventId);

    /**
     * Count of waitlisted users
     *
     * @param eventId
     * @return
     */
    Rsvp getFirstWaitlisted(int eventId);

    /**
     * Get all RSVPs made by a user — used to build recommendation exclusion list
     *
     * @param userEmail
     * @return
     */
    List<Rsvp> getRsvpsByUser(String userEmail);
}
