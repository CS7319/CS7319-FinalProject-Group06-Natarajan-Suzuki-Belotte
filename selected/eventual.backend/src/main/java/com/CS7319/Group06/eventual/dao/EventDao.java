package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Event;

import java.util.List;

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
     * Delete an event
     *
     * @param id
     * @return
     */
    int deleteEventById(int id);

    /**
     * Get all events paginated — used by the reindex endpoint.
     *
     * @param page zero-based page number
     * @param size page size
     * @return list of events
     */
    List<Event> getEventsPaginated(int page, int size);

    /**
     * Count total number of events.
     *
     * @return total count
     */
    int countEvents();
}
