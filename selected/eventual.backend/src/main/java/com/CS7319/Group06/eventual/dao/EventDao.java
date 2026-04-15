package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Event;

/**
 * Data layer for events
 *
 * @author harininatarajan
 */
public interface EventDao {

    /**
     * Get an event by id
     *
     * @param id
     * @return
     */
    Event getEventById(int id);

    /**
     * Create a new event
     *
     * @param event
     * @return
     */
    Event createEvent(Event event);

    /**
     * Update an event
     *
     * @param event
     * @return
     */
    Event updateEvent(Event event);

    /**
     * Delete an event
     *
     * @param id
     * @return
     */
    int deleteEventById(int id);
}
