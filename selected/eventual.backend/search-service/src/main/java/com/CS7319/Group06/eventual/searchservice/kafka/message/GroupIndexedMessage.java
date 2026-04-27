package com.CS7319.Group06.eventual.searchservice.kafka.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * Kafka message received when a group is created or updated by the Event Service.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupIndexedMessage {

    private int groupId;
    private String name;
    private String description;
    private String ownerEmail;
    private boolean isPublic;
    private int memberCount;
}
