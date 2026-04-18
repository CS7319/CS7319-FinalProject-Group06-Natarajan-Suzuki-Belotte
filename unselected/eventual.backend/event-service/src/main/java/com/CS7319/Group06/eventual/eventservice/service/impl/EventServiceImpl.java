package com.CS7319.Group06.eventual.eventservice.service.impl;

import com.CS7319.Group06.eventual.eventservice.config.FileStorageConfig;
import com.CS7319.Group06.eventual.eventservice.dao.EventDao;
import com.CS7319.Group06.eventual.eventservice.dao.RsvpDao;
import com.CS7319.Group06.eventual.eventservice.exception.DaoException;
import com.CS7319.Group06.eventual.eventservice.kafka.EventServiceKafkaProducer;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventCreatedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventDeletedMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.EventUpdatedMessage;
import com.CS7319.Group06.eventual.eventservice.model.Event;
import com.CS7319.Group06.eventual.eventservice.model.Rsvp;
import com.CS7319.Group06.eventual.eventservice.model.constants.EventType;
import com.CS7319.Group06.eventual.eventservice.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Implementation for EventService
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;
    private final RsvpDao rsvpDao;
    private final FileStorageConfig fileStorageConfig;
    private final EventServiceKafkaProducer kafkaProducer;

    public EventServiceImpl(EventDao eventDao, RsvpDao rsvpDao,
                            FileStorageConfig fileStorageConfig,
                            EventServiceKafkaProducer kafkaProducer) {
        this.eventDao = eventDao;
        this.rsvpDao = rsvpDao;
        this.fileStorageConfig = fileStorageConfig;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public Event getEventById(int id) {
        try {
            Event event = eventDao.getEventById(id);
            if (event == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + id);
            }
            return event;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Event createEvent(Event event, MultipartFile picture, String organizerEmail) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        if (event.getLocation() == null || event.getLocation().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required");
        }
        if (event.getStartDateTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date/time is required");
        }
        if (event.getEndDateTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date/time is required");
        }
        if (!event.getEndDateTime().isAfter(event.getStartDateTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date/time must be after start date/time");
        }
        if (EventType.GROUP == event.getEventType() && event.getGroupId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A group event must specify a groupId");
        }
        if (EventType.PUBLIC == event.getEventType() && event.getGroupId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A public event cannot be linked to a group");
        }

        event.setOrganizerEmail(organizerEmail);

        if (picture != null && !picture.isEmpty()) {
            event.setEventPicture(storeEventPicture(organizerEmail, picture));
        }

        try {
            Event created = eventDao.createEvent(event);

            kafkaProducer.publishEventCreated(new EventCreatedMessage(
                    created.getEventId(),
                    created.getTitle(),
                    created.getDescription(),
                    created.getLocation(),
                    created.getStartDateTime() != null ? created.getStartDateTime().toString() : null,
                    created.getEndDateTime() != null ? created.getEndDateTime().toString() : null,
                    created.getOrganizerEmail(),
                    created.getOrganizerName(),
                    created.getEventType().name(),
                    created.getGroupId(),
                    created.getCategoryTypes(),
                    created.getCapacity()));

            return created;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Event updateEvent(int id, Event event, MultipartFile picture, String organizerEmail) {
        Event existing;
        try {
            existing = eventDao.getEventById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + id);
        }
        if (!existing.getOrganizerEmail().equals(organizerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the organizer of this event can edit it");
        }

        event.setEventId(id);
        event.setOrganizerEmail(organizerEmail);
        event.setModifiedBy(organizerEmail);

        if (picture != null && !picture.isEmpty()) {
            event.setEventPicture(storeEventPicture(organizerEmail, picture));
        }

        try {
            Event updated = eventDao.updateEvent(event);
            if (updated == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + id);
            }

            // Fetch RSVPed emails then publish Kafka message with full event data for ES re-indexing
            List<String> rsvpedEmails = rsvpDao.getRsvpsByEvent(id).stream()
                    .map(Rsvp::getUserEmail)
                    .toList();
            kafkaProducer.publishEventUpdated(new EventUpdatedMessage(
                    id, updated.getTitle(), rsvpedEmails,
                    updated.getDescription(), updated.getLocation(),
                    updated.getStartDateTime() != null ? updated.getStartDateTime().toString() : null,
                    updated.getEndDateTime() != null ? updated.getEndDateTime().toString() : null,
                    updated.getOrganizerEmail(), updated.getOrganizerName(),
                    updated.getEventType() != null ? updated.getEventType().name() : null,
                    updated.getGroupId(), updated.getCategoryTypes(), updated.getCapacity()));

            return updated;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteEvent(int id, String organizerEmail) {
        Event existing;
        try {
            existing = eventDao.getEventById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + id);
        }
        if (!existing.getOrganizerEmail().equals(organizerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the organizer of this event can delete it");
        }

        // Fetch RSVPed emails before deletion
        List<String> rsvpedEmails = rsvpDao.getRsvpsByEvent(id).stream()
                .map(Rsvp::getUserEmail)
                .toList();

        try {
            int rows = eventDao.deleteEventById(id);
            if (rows == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + id);
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        // Notify RSVPed users after successful deletion
        kafkaProducer.publishEventDeleted(new EventDeletedMessage(id, existing.getTitle(), rsvpedEmails));
    }

    private String storeEventPicture(String organizerEmail, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed for event picture");
        }
        try {
            Path eventDir = Paths.get(fileStorageConfig.getUploadDir(), "event-pictures", organizerEmail);
            Files.createDirectories(eventDir);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), eventDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "event-pictures/" + organizerEmail + "/" + filename;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store event picture");
        }
    }
}
