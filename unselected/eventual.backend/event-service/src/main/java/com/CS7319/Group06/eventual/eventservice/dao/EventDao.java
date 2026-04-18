package com.CS7319.Group06.eventual.eventservice.dao;

import com.CS7319.Group06.eventual.eventservice.model.Event;

/**
 * Data layer for events
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
     * Delete an event by id
     *
     * @param id
     * @return rows affected
     */
    int deleteEventById(int id);
}
