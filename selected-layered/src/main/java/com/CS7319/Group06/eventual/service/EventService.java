package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.Event;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for event operations
 *
 * @author harininatarajan
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
     */
    void deleteEvent(int id);
}
