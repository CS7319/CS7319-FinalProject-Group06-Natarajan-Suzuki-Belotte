package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JoinRequestSubmittedMessage - join request submitted message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestSubmittedMessage {
    private int requestId;
    private int groupId;
    private String groupName;
    private String ownerEmail;
    private String requesterEmail;
}
