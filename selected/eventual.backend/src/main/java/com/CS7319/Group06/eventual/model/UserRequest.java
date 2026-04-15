package com.CS7319.Group06.eventual.model;

import com.CS7319.Group06.eventual.model.constants.UserRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request class for user registration and profile update.
 *
 * @author harininatarajan
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequest {

    @Email(message = "Must be a valid email address")
    private String email;

    @Size(max = 256)
    private String name;

    @Size(max = 50)
    private String pronoun;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private UserRole role;

    @Size(max = 256)
    private String location;

    @Size(max = 1000)
    private String aboutMe;

    @Size(max = 5, message = "You may select at most 5 category types")
    private List<String> categoryTypes;

    private List<Integer> groupIds;
}
