package com.CS7319.Group06.eventual.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User data object
 *
 * @author harininatarajan
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {

    private String email;
    private String name;
    private String pronoun;

    @JsonIgnore
    private String passwordHash;

    private String role;
    private String profilePicturePath;
    private String location;
    private String aboutMe;
    private List<Integer> categoryIds;
    private List<Integer> groupIds;
    private LocalDateTime createdAt;
}
