package com.CS7319.Group06.eventual.notificationservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JoinRequestRejectedMessage - join request rejected message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestRejectedMessage {
    private int groupId;
    private String groupName;
    private String requesterEmail;
}
