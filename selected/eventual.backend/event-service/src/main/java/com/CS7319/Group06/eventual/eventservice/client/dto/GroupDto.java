package com.CS7319.Group06.eventual.eventservice.client.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * GroupDto - data model representing group.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupDto {

    private int groupId;

    private String name;

    private Boolean isPublic;

    private String ownerEmail;

    private List<String> memberEmails;
}
