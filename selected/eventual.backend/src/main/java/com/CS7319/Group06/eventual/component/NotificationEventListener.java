package com.CS7319.Group06.eventual.component;

import com.CS7319.Group06.eventual.dao.GroupDao;
import com.CS7319.Group06.eventual.dao.NotificationDao;
import com.CS7319.Group06.eventual.dao.RsvpDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.Notification;
import com.CS7319.Group06.eventual.model.Rsvp;
import com.CS7319.Group06.eventual.model.constants.NotificationReferenceType;
import com.CS7319.Group06.eventual.model.constants.NotificationType;
import com.CS7319.Group06.eventual.model.notification.EventCancelledNotification;
import com.CS7319.Group06.eventual.model.notification.EventUpdatedNotification;
import com.CS7319.Group06.eventual.model.notification.JoinRequestApprovedNotification;
import com.CS7319.Group06.eventual.model.notification.JoinRequestRejectedNotification;
import com.CS7319.Group06.eventual.model.notification.JoinRequestSubmittedNotification;
import com.CS7319.Group06.eventual.model.notification.NewGroupEventCreatedNotification;
import com.CS7319.Group06.eventual.model.notification.RsvpCancelledNotification;
import com.CS7319.Group06.eventual.model.notification.RsvpCreatedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listens for domain events published via ApplicationEventPublisher and persists the corresponding notifications asynchronously.
 *
 * @author harininatarajan
 */
@Slf4j
@Component
public class NotificationEventListener {

    private final NotificationDao notificationDao;
    private final RsvpDao rsvpDao;
    private final GroupDao groupDao;

    public NotificationEventListener(NotificationDao notificationDao,
                                     RsvpDao rsvpDao,
                                     GroupDao groupDao) {
        this.notificationDao = notificationDao;
        this.rsvpDao = rsvpDao;
        this.groupDao = groupDao;
    }

    //Notify the event organizer when someone RSVPs
    @Async("notificationExecutor")
    @EventListener
    public void onRsvpCreated(RsvpCreatedNotification event) {
        send(event.getOrganizerEmail(), NotificationType.RSVP_CREATED, "New RSVP for your event",
                event.getUserEmail() + " RSVPed to your event \"" + event.getEventTitle()
                        + "\" with status " + event.getStatus(),
                String.valueOf(event.getEventId()), NotificationReferenceType.EVENT);
    }

    //Notify the event organizer when someone cancels their RSVP
    @Async("notificationExecutor")
    @EventListener
    public void onRsvpCancelled(RsvpCancelledNotification event) {
        send(event.getOrganizerEmail(), NotificationType.RSVP_CANCELLED, "RSVP cancelled",
                event.getUserEmail() + " cancelled their RSVP for \"" + event.getEventTitle() + "\"",
                String.valueOf(event.getEventId()), NotificationReferenceType.EVENT);
    }

    //Notify all RSVPed users when an event is updated
    @Async("notificationExecutor")
    @EventListener
    public void onEventUpdated(EventUpdatedNotification event) {
        try {
            List<Rsvp> rsvps = rsvpDao.getRsvpsByEvent(event.getEventId());
            rsvps.forEach(rsvp -> send(rsvp.getUserEmail(), NotificationType.EVENT_UPDATED, "Event updated",
                    "\"" + event.getEventTitle() + "\" has been updated. Check the latest details.",
                    String.valueOf(event.getEventId()), NotificationReferenceType.EVENT));
        } catch (DaoException e) {
            log.warn("Failed to fetch RSVPs for EventUpdatedNotification eventId={}: {}", event.getEventId(), e.getMessage());
        }
    }

    //Notify all RSVPed users that the event has been cancelled
    @Async("notificationExecutor")
    @EventListener
    public void onEventCancelled(EventCancelledNotification event) {
        event.getRsvpedEmails().forEach(email -> send(email, NotificationType.EVENT_CANCELLED, "Event cancelled",
                "\"" + event.getEventTitle() + "\" has been cancelled.",
                String.valueOf(event.getEventId()), NotificationReferenceType.EVENT));
    }

    //Notify all group members when a new group-type event is created
    @Async("notificationExecutor")
    @EventListener
    public void onNewGroupEvent(NewGroupEventCreatedNotification event) {
        try {
            Group group = groupDao.getGroupById(event.getGroupId());
            if (group == null || group.getMemberEmails() == null) return;

            group.getMemberEmails().stream()
                    .filter(email -> !email.equals(event.getOrganizerEmail()))
                    .forEach(email -> send(email, NotificationType.NEW_GROUP_EVENT, "New event in your group",
                            "A new event \"" + event.getEventTitle() + "\" has been created in your group.",
                            String.valueOf(event.getEventId()), NotificationReferenceType.EVENT));
        } catch (DaoException e) {
            log.warn("Failed to fetch group for NewGroupEventCreatedNotification groupId={}: {}", event.getGroupId(), e.getMessage());
        }
    }

    //Notify the group owner when someone requests to join their group
    @Async("notificationExecutor")
    @EventListener
    public void onJoinRequestSubmitted(JoinRequestSubmittedNotification event) {
        send(event.getOwnerEmail(), NotificationType.JOIN_REQUEST_SUBMITTED, "New join request",
                event.getRequesterEmail() + " wants to join your group \"" + event.getGroupName() + "\"",
                String.valueOf(event.getRequestId()), NotificationReferenceType.JOIN_REQUEST);
    }

    //Notify the requester when their join request is approved
    @Async("notificationExecutor")
    @EventListener
    public void onJoinRequestApproved(JoinRequestApprovedNotification event) {
        send(event.getRequesterEmail(), NotificationType.JOIN_REQUEST_APPROVED, "Join request approved",
                "Your request to join \"" + event.getGroupName() + "\" has been approved. Welcome!",
                String.valueOf(event.getGroupId()), NotificationReferenceType.GROUP);
    }

    //Notify the requester when their join request is rejected
    @Async("notificationExecutor")
    @EventListener
    public void onJoinRequestRejected(JoinRequestRejectedNotification event) {
        send(event.getRequesterEmail(), NotificationType.JOIN_REQUEST_REJECTED, "Join request not approved",
                "Your request to join \"" + event.getGroupName() + "\" was not approved.",
                String.valueOf(event.getGroupId()), NotificationReferenceType.GROUP);
    }

    private void send(String recipientEmail, NotificationType type, String title, String message,
                      String referenceId, NotificationReferenceType referenceType) {
        try {
            Notification notification = new Notification();
            notification.setRecipientEmail(recipientEmail);
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setReferenceId(referenceId);
            notification.setReferenceType(referenceType);
            notificationDao.createNotification(notification);
            log.debug("Notification [{}] sent to {}", type, recipientEmail);
        } catch (DaoException e) {
            // Notification failure must never crash the main flow
            log.warn("Failed to persist notification [{}] for {}: {}", type, recipientEmail, e.getMessage());
        }
    }
}
