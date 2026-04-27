package com.CS7319.Group06.eventual.userservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupDeletedMessage - group deleted message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDeletedMessage {

    private int groupId;
}
