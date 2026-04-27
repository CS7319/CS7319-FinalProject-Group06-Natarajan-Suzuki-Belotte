package com.CS7319.Group06.eventual.userservice.kafka;

import com.CS7319.Group06.eventual.userservice.kafka.message.GroupDeletedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.GroupIndexedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.JoinRequestApprovedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.JoinRequestRejectedMessage;
import com.CS7319.Group06.eventual.userservice.kafka.message.JoinRequestSubmittedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * UserServiceKafkaProducer - user service kafka producer.
 */
@Slf4j
@Component
public class UserServiceKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topicGroupIndexed;
    private final String topicGroupDeleted;
    private final String topicJoinRequestSubmitted;
    private final String topicJoinRequestApproved;
    private final String topicJoinRequestRejected;

    public UserServiceKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.group-indexed}") String topicGroupIndexed,
            @Value("${kafka.topics.group-deleted}") String topicGroupDeleted,
            @Value("${kafka.topics.join-request-submitted}") String topicJoinRequestSubmitted,
            @Value("${kafka.topics.join-request-approved}") String topicJoinRequestApproved,
            @Value("${kafka.topics.join-request-rejected}") String topicJoinRequestRejected) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicGroupIndexed = topicGroupIndexed;
        this.topicGroupDeleted = topicGroupDeleted;
        this.topicJoinRequestSubmitted = topicJoinRequestSubmitted;
        this.topicJoinRequestApproved = topicJoinRequestApproved;
        this.topicJoinRequestRejected = topicJoinRequestRejected;
    }

    public void publishGroupIndexed(GroupIndexedMessage msg) {
        kafkaTemplate.send(topicGroupIndexed, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published group-indexed for groupId={}", msg.getGroupId());
    }

    public void publishGroupDeleted(GroupDeletedMessage msg) {
        kafkaTemplate.send(topicGroupDeleted, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published group-deleted for groupId={}", msg.getGroupId());
    }

    public void publishJoinRequestSubmitted(JoinRequestSubmittedMessage msg) {
        kafkaTemplate.send(topicJoinRequestSubmitted, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published join-request-submitted for requestId={}", msg.getRequestId());
    }

    public void publishJoinRequestApproved(JoinRequestApprovedMessage msg) {
        kafkaTemplate.send(topicJoinRequestApproved, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published join-request-approved for groupId={}, requester={}", msg.getGroupId(), msg.getRequesterEmail());
    }

    public void publishJoinRequestRejected(JoinRequestRejectedMessage msg) {
        kafkaTemplate.send(topicJoinRequestRejected, String.valueOf(msg.getGroupId()), msg);
        log.debug("Published join-request-rejected for groupId={}, requester={}", msg.getGroupId(), msg.getRequesterEmail());
    }
}
