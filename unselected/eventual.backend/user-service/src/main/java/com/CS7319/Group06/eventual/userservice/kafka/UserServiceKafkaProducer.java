package com.CS7319.Group06.eventual.userservice.kafka;

import com.CS7319.Group06.eventual.userservice.kafka.message.GroupDeletedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.GroupIndexedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.JoinRequestApprovedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.JoinRequestRejectedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.JoinRequestSubmittedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * UserServiceKafkaProducer - user service kafka producer.
 */
@Slf4j
@Component
public class UserServiceKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserServiceKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishGroupIndexed(GroupIndexedMessage msg) {
        kafkaTemplate.send(KafkaTopics.GROUP_INDEXED, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published group-indexed for groupId={}", msg.getGroupId());
    }

    public void publishGroupDeleted(GroupDeletedMessage msg) {
        kafkaTemplate.send(KafkaTopics.GROUP_DELETED, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published group-deleted for groupId={}", msg.getGroupId());
    }

    public void publishJoinRequestSubmitted(JoinRequestSubmittedMessage msg) {
        kafkaTemplate.send(KafkaTopics.JOIN_REQUEST_SUBMITTED, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published join-request-submitted for requestId={}", msg.getRequestId());
    }

    public void publishJoinRequestApproved(JoinRequestApprovedMessage msg) {
        kafkaTemplate.send(KafkaTopics.JOIN_REQUEST_APPROVED, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published join-request-approved for groupId={}, requester={}", msg.getGroupId(), msg.getRequesterEmail());
    }

    public void publishJoinRequestRejected(JoinRequestRejectedMessage msg) {
        kafkaTemplate.send(KafkaTopics.JOIN_REQUEST_REJECTED, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published join-request-rejected for groupId={}, requester={}", msg.getGroupId(), msg.getRequesterEmail());
    }
}
