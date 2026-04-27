package com.CS7319.Group06.eventual.model;

import com.CS7319.Group06.eventual.model.constants.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User data object
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {

    private String email;

    private String name;

    private String pronoun;

    @JsonIgnore
    private String passwordHash;

    private UserRole role;

    private String profilePicturePath;

    private String location;

    private String aboutMe;

    private List<String> categoryTypes;

    private List<Integer> groupIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
