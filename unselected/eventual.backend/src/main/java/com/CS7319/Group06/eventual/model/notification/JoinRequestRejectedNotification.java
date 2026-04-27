package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Published when a group owner rejects a join request.
 */
@Data
@AllArgsConstructor
public class JoinRequestRejectedNotification {

    private int groupId;

    private String groupName;

    private String requesterEmail;
}
