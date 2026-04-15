package com.CS7319.Group06.eventual.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Published when a user submits a request to join a private group.
 *
 * @author harininatarajan
 */
@Data
@AllArgsConstructor
public class JoinRequestSubmittedNotification {

    private int requestId;

    private int groupId;

    private String groupName;

    private String ownerEmail;

    private String requesterEmail;
}
