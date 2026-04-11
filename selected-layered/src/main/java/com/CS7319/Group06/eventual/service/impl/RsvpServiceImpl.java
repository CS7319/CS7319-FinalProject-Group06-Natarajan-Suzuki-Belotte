package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.EventDao;
import com.CS7319.Group06.eventual.dao.GroupDao;
import com.CS7319.Group06.eventual.dao.RsvpDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Event;
import com.CS7319.Group06.eventual.model.constants.EventType;
import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.Rsvp;
import com.CS7319.Group06.eventual.model.constants.RsvpStatus;
import com.CS7319.Group06.eventual.service.RsvpService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implementation for RsvpService
 *
 * @author harininatarajan
 */
@Service
public class RsvpServiceImpl implements RsvpService {

    private final RsvpDao rsvpDao;
    private final EventDao eventDao;
    private final GroupDao groupDao;

    public RsvpServiceImpl(RsvpDao rsvpDao, EventDao eventDao, GroupDao groupDao) {
        this.rsvpDao = rsvpDao;
        this.eventDao = eventDao;
        this.groupDao = groupDao;
    }

    @Override
    public Rsvp rsvpToEvent(int eventId, String userEmail) {
        Event event = getEvent(eventId);

        // Group event, only members can RSVP
        if (EventType.GROUP == event.getEventType()) {
            Group group = groupDao.getGroupById(event.getGroupId());
            if (group == null || !group.getMemberEmails().contains(userEmail)) {
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
            if (existing != null) {
                // Re-RSVP after cancellation
                return rsvpDao.updateRsvpStatus(eventId, userEmail, status);
            } else {
                Rsvp rsvp = new Rsvp();
                rsvp.setEventId(eventId);
                rsvp.setUserEmail(userEmail);
                rsvp.setStatus(status);
                return rsvpDao.createRsvp(rsvp);
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Rsvp cancelRsvp(int eventId, String userEmail) {
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

    /**
     * If capacity is 0 the event is unlimited; otherwise compare current GOING count.
     */
    private RsvpStatus resolveStatus(Event event) {
        if (event.getCapacity() == 0) {
            return RsvpStatus.GOING;
        }
        int goingCount = rsvpDao.countGoingByEvent(event.getEventId());
        return goingCount < event.getCapacity() ? RsvpStatus.GOING : RsvpStatus.WAITLISTED;
    }
}
