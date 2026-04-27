package com.CS7319.Group06.eventual.eventservice.service;

import com.CS7319.Group06.eventual.eventservice.model.Event;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for event operations
 */
public interface EventService {

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
     * @param picture
     * @param organizerEmail
     * @return
     */
    Event createEvent(Event event, MultipartFile picture, String organizerEmail);

    /**
     * Update an event
     *
     * @param id
     * @param event
     * @param picture
     * @param organizerEmail
     * @return
     */
    Event updateEvent(int id, Event event, MultipartFile picture, String organizerEmail);

    /**
     * Delete an event
     *
     * @param id
     * @param organizerEmail
     */
    void deleteEvent(int id, String organizerEmail);

    /**
     * Get all events paginated — used by the search-service reindex endpoint.
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
