package com.CS7319.Group06.eventual.eventservice.service.impl;

import com.CS7319.Group06.eventual.eventservice.client.UserServiceClient;
import com.CS7319.Group06.eventual.eventservice.client.dto.GroupDto;
import com.CS7319.Group06.eventual.eventservice.dao.EventDao;
import com.CS7319.Group06.eventual.eventservice.dao.RsvpDao;
import com.CS7319.Group06.eventual.eventservice.exception.DaoException;
import com.CS7319.Group06.eventual.eventservice.kafka.EventServiceKafkaProducer;
import com.CS7319.Group06.eventual.eventservice.kafka.message.RsvpCancelledMessage;
import com.CS7319.Group06.eventual.eventservice.kafka.message.RsvpCreatedMessage;
import com.CS7319.Group06.eventual.eventservice.model.Event;
import com.CS7319.Group06.eventual.eventservice.model.Rsvp;
import com.CS7319.Group06.eventual.eventservice.model.constants.EventType;
import com.CS7319.Group06.eventual.eventservice.model.constants.RsvpStatus;
import com.CS7319.Group06.eventual.eventservice.service.RsvpService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implementation for RsvpService
 */
@Service
public class RsvpServiceImpl implements RsvpService {

    private final RsvpDao rsvpDao;
    private final EventDao eventDao;
    private final UserServiceClient userServiceClient;
    private final EventServiceKafkaProducer kafkaProducer;

    public RsvpServiceImpl(RsvpDao rsvpDao, EventDao eventDao,
                           UserServiceClient userServiceClient,
                           EventServiceKafkaProducer kafkaProducer) {
        this.rsvpDao = rsvpDao;
        this.eventDao = eventDao;
        this.userServiceClient = userServiceClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public Rsvp rsvpToEvent(int eventId, String userEmail) {
        Event event = getEvent(eventId);

        // Group event: only members can RSVP
        if (EventType.GROUP == event.getEventType()) {
            GroupDto group = userServiceClient.getGroupById(event.getGroupId());
            if (group == null || group.getMemberEmails() == null || !group.getMemberEmails().contains(userEmail)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only members of the group can RSVP to this event");
            }
        }

        Rsvp existing = rsvpDao.getRsvpByEventAndUser(eventId, userEmail);
        if (existing != null && RsvpStatus.CANCELLED != existing.getStatus()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already RSVPed to this event with status: " + existing.getStatus());
        }

        RsvpStatus status = resolveStatus(event);

        try {
            Rsvp rsvp;
            if (existing != null) {
                // Re-RSVP after cancellation
                rsvp = rsvpDao.updateRsvpStatus(eventId, userEmail, status);
            } else {
                Rsvp newRsvp = new Rsvp();
                newRsvp.setEventId(eventId);
                newRsvp.setUserEmail(userEmail);
                newRsvp.setStatus(status);
                rsvp = rsvpDao.createRsvp(newRsvp);
            }

            // Notify the organizer
            kafkaProducer.publishRsvpCreated(new RsvpCreatedMessage(
                    eventId, event.getTitle(), event.getOrganizerEmail(), userEmail, status.name()));

            return rsvp;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Rsvp cancelRsvp(int eventId, String userEmail) {
        Event event = getEvent(eventId);

        Rsvp existing = rsvpDao.getRsvpByEventAndUser(eventId, userEmail);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No RSVP found for this event");
        }
        if (RsvpStatus.CANCELLED == existing.getStatus()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Your RSVP is already cancelled");
        }

        RsvpStatus previousStatus = existing.getStatus();

        try {
            Rsvp cancelled = rsvpDao.updateRsvpStatus(eventId, userEmail, RsvpStatus.CANCELLED);

            // A GOING spot opened up — promote the earliest waitlisted person
            if (RsvpStatus.GOING == previousStatus) {
                Rsvp firstWaitlisted = rsvpDao.getFirstWaitlisted(eventId);
                if (firstWaitlisted != null) {
                    rsvpDao.updateRsvpStatus(eventId, firstWaitlisted.getUserEmail(), RsvpStatus.GOING);
                }
            }

            // Notify the organizer
            kafkaProducer.publishRsvpCancelled(new RsvpCancelledMessage(
                    eventId, event.getTitle(), event.getOrganizerEmail(), userEmail));

            return cancelled;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<Rsvp> getRsvpsForEvent(int eventId) {
        getEvent(eventId);
        try {
            return rsvpDao.getRsvpsByEvent(eventId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Event getEvent(int eventId) {
        try {
            Event event = eventDao.getEventById(eventId);
            if (event == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Event not found with id: " + eventId);
            }
            return event;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // If capacity is 0 the event is unlimited; otherwise compare current GOING count.
    private RsvpStatus resolveStatus(Event event) {
        if (event.getCapacity() == 0) {
            return RsvpStatus.GOING;
        }
        int goingCount = rsvpDao.countGoingByEvent(event.getEventId());
        return goingCount < event.getCapacity() ? RsvpStatus.GOING : RsvpStatus.WAITLISTED;
    }
}
