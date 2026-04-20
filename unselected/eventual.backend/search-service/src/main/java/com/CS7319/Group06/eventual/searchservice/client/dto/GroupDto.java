package com.CS7319.Group06.eventual.searchservice.client.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * DTO for deserializing Group responses from user-service.
 * Field names are snake_case (user-service uses @JsonNaming SnakeCaseStrategy).
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupDto {
    private int groupId;
    private String name;
    private String description;
    private String ownerEmail;
    private Boolean isPublic;
    private List<String> memberEmails;
}
