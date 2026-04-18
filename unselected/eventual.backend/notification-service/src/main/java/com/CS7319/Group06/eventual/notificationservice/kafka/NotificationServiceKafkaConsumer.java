package com.CS7319.Group06.eventual.notificationservice.kafka;

import com.CS7319.Group06.eventual.notificationservice.client.UserServiceClient;
import com.CS7319.Group06.eventual.notificationservice.client.dto.GroupDto;
import com.CS7319.Group06.eventual.notificationservice.dao.NotificationDao;
import com.CS7319.Group06.eventual.notificationservice.exception.DaoException;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.EventCreatedMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.EventDeletedMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.EventUpdatedMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.JoinRequestApprovedMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.JoinRequestRejectedMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.JoinRequestSubmittedMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.RsvpCancelledMessage;
import com.CS7319.Group06.eventual.notificationservice.kafka.message.RsvpCreatedMessage;
import com.CS7319.Group06.eventual.notificationservice.model.Notification;
import com.CS7319.Group06.eventual.notificationservice.model.constants.NotificationReferenceType;
import com.CS7319.Group06.eventual.notificationservice.model.constants.NotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * NotificationServiceKafkaConsumer - notification service kafka consumer.
 */
@Slf4j
@Component
public class NotificationServiceKafkaConsumer {

    private final NotificationDao notificationDao;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    public NotificationServiceKafkaConsumer(NotificationDao notificationDao,
                                            UserServiceClient userServiceClient,
                                            ObjectMapper objectMapper) {
        this.notificationDao = notificationDao;
        this.userServiceClient = userServiceClient;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "rsvp-created", groupId = "notification-service")
    public void onRsvpCreated(Object payload) {
        RsvpCreatedMessage msg = objectMapper.convertValue(payload, RsvpCreatedMessage.class);
        send(msg.getOrganizerEmail(), NotificationType.RSVP_CREATED,
                "New RSVP for your event",
                msg.getUserEmail() + " RSVPed to your event \"" + msg.getEventTitle() + "\" with status " + msg.getStatus(),
                String.valueOf(msg.getEventId()), NotificationReferenceType.EVENT);
    }

    @KafkaListener(topics = "rsvp-cancelled", groupId = "notification-service")
    public void onRsvpCancelled(Object payload) {
        RsvpCancelledMessage msg = objectMapper.convertValue(payload, RsvpCancelledMessage.class);
        send(msg.getOrganizerEmail(), NotificationType.RSVP_CANCELLED,
                "RSVP cancelled",
                msg.getUserEmail() + " cancelled their RSVP for \"" + msg.getEventTitle() + "\"",
                String.valueOf(msg.getEventId()), NotificationReferenceType.EVENT);
    }

    @KafkaListener(topics = "event-updated", groupId = "notification-service")
    public void onEventUpdated(Object payload) {
        EventUpdatedMessage msg = objectMapper.convertValue(payload, EventUpdatedMessage.class);
        if (msg.getRsvpedEmails() == null) return;
        msg.getRsvpedEmails().forEach(email ->
                send(email, NotificationType.EVENT_UPDATED,
                        "Event updated",
                        "\"" + msg.getTitle() + "\" has been updated. Check the latest details.",
                        String.valueOf(msg.getEventId()), NotificationReferenceType.EVENT));
    }

    @KafkaListener(topics = "event-deleted", groupId = "notification-service")
    public void onEventDeleted(Object payload) {
        EventDeletedMessage msg = objectMapper.convertValue(payload, EventDeletedMessage.class);
        if (msg.getRsvpedEmails() == null) return;
        msg.getRsvpedEmails().forEach(email ->
                send(email, NotificationType.EVENT_CANCELLED,
                        "Event cancelled",
                        "\"" + msg.getTitle() + "\" has been cancelled.",
                        String.valueOf(msg.getEventId()), NotificationReferenceType.EVENT));
    }

    @KafkaListener(topics = "event-created", groupId = "notification-service")
    public void onEventCreated(Object payload) {
        EventCreatedMessage msg = objectMapper.convertValue(payload, EventCreatedMessage.class);
        // Only notify for GROUP events
        if (!"GROUP".equals(msg.getEventType()) || msg.getGroupId() == null) return;

        // Notification Service is responsible for resolving group members
        GroupDto group = userServiceClient.getGroupById(msg.getGroupId());
        if (group == null || group.getMemberEmails() == null) return;

        group.getMemberEmails().stream()
                .filter(email -> !email.equals(msg.getOrganizerEmail()))
                .forEach(email ->
                        send(email, NotificationType.NEW_GROUP_EVENT,
                                "New event in your group",
                                "A new event \"" + msg.getTitle() + "\" has been created in your group.",
                                String.valueOf(msg.getEventId()), NotificationReferenceType.EVENT));
    }

    @KafkaListener(topics = "join-request-submitted", groupId = "notification-service")
    public void onJoinRequestSubmitted(Object payload) {
        JoinRequestSubmittedMessage msg = objectMapper.convertValue(payload, JoinRequestSubmittedMessage.class);
        send(msg.getOwnerEmail(), NotificationType.JOIN_REQUEST_SUBMITTED,
                "New join request",
                msg.getRequesterEmail() + " wants to join your group \"" + msg.getGroupName() + "\"",
                String.valueOf(msg.getRequestId()), NotificationReferenceType.JOIN_REQUEST);
    }

    @KafkaListener(topics = "join-request-approved", groupId = "notification-service")
    public void onJoinRequestApproved(Object payload) {
        JoinRequestApprovedMessage msg = objectMapper.convertValue(payload, JoinRequestApprovedMessage.class);
        send(msg.getRequesterEmail(), NotificationType.JOIN_REQUEST_APPROVED,
                "Join request approved",
                "Your request to join \"" + msg.getGroupName() + "\" has been approved. Welcome!",
                String.valueOf(msg.getGroupId()), NotificationReferenceType.GROUP);
    }

    @KafkaListener(topics = "join-request-rejected", groupId = "notification-service")
    public void onJoinRequestRejected(Object payload) {
        JoinRequestRejectedMessage msg = objectMapper.convertValue(payload, JoinRequestRejectedMessage.class);
        send(msg.getRequesterEmail(), NotificationType.JOIN_REQUEST_REJECTED,
                "Join request not approved",
                "Your request to join \"" + msg.getGroupName() + "\" was not approved.",
                String.valueOf(msg.getGroupId()), NotificationReferenceType.GROUP);
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
            log.warn("Failed to persist notification [{}] for {}: {}", type, recipientEmail, e.getMessage());
        }
    }
}
