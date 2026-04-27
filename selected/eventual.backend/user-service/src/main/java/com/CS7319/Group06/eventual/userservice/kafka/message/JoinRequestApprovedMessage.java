package com.CS7319.Group06.eventual.userservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JoinRequestApprovedMessage - join request approved message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestApprovedMessage {

    private int groupId;
    private String groupName;
    private String requesterEmail;
}
