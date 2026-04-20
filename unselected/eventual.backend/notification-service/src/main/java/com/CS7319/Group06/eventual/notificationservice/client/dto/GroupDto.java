package com.CS7319.Group06.eventual.notificationservice.client.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * Minimal group representation returned by the User Service internal API.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupDto {
    private int groupId;
    private String name;
    private String ownerEmail;
    private Boolean isPublic;
    private List<String> memberEmails;
}
