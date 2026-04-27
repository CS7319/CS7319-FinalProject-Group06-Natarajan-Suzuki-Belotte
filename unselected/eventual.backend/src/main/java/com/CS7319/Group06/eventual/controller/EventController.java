package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.Event;
import com.CS7319.Group06.eventual.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PreAuthorize("hasRole('ORGANIZER')")
    public Event createEvent(@Valid @ModelAttribute Event event,
                             @RequestParam(required = false) MultipartFile eventPicture,
                             Authentication authentication) {
        return eventService.createEvent(event, eventPicture, authentication.getName());
    }

    @PutMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ORGANIZER')")
    public Event updateEvent(@PathVariable int id,
                             @Valid @ModelAttribute Event event,
                             @RequestParam(required = false) MultipartFile eventPicture,
                             Authentication authentication) {
        return eventService.updateEvent(id, event, eventPicture, authentication.getName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public void deleteEvent(@PathVariable int id, Authentication authentication) {
        eventService.deleteEvent(id, authentication.getName());
    }
}
