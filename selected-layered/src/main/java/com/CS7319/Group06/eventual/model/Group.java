package com.CS7319.Group06.eventual.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a community group a user can join
 *
 * @author harininatarajan
 */
@Data
public class Group {

    private int groupId;
    private String name;
    private String description;
    private String creatorEmail;
    private String ownerEmail;
    private Boolean isPublic;
    private List<String> memberEmails;
    private LocalDateTime createdAt;
}
