package com.CS7319.Group06.eventual.userservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupIndexedMessage - group indexed message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupIndexedMessage {

    private int groupId;
    private String name;
    private String description;
    private String ownerEmail;
    private boolean isPublic;
    private int memberCount;
}
