package com.CS7319.Group06.eventual.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a community group a user can join
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Group {

    private int groupId;

    private String name;

    private String description;

    private String creatorEmail;

    private String ownerEmail;

    private Boolean isPublic;

    private List<String> memberEmails;

    private String modifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
