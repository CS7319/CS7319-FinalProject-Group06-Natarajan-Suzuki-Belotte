package com.CS7319.Group06.eventual.userservice.model;

import com.CS7319.Group06.eventual.userservice.model.constants.JoinRequestStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Class for join request
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupJoinRequest {

    private int id;

    private int groupId;

    private String requesterEmail;

    private JoinRequestStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
