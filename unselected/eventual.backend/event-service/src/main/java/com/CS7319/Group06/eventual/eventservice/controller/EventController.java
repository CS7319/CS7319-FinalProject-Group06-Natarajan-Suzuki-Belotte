package com.CS7319.Group06.eventual.eventservice.controller;

import com.CS7319.Group06.eventual.eventservice.model.Event;
import com.CS7319.Group06.eventual.eventservice.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Event Controller — create/update restricted to ORGANIZERs only
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable int id) {
        return eventService.getEventById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public Event createEvent(@ModelAttribute Event event,
                             @RequestParam(required = false) MultipartFile eventPicture,
                             @RequestHeader(value = "X-Authenticated-User", required = false) String userEmail,
                             @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return eventService.createEvent(event, eventPicture, userEmail);
    }

    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public Event updateEvent(@PathVariable int id,
                             @ModelAttribute Event event,
                             @RequestParam(required = false) MultipartFile eventPicture,
                             @RequestHeader(value = "X-Authenticated-User", required = false) String userEmail,
                             @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return eventService.updateEvent(id, event, eventPicture, userEmail);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id,
                            @RequestHeader(value = "X-Authenticated-User", required = false) String userEmail,
                            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        eventService.deleteEvent(id, userEmail);
    }
}
