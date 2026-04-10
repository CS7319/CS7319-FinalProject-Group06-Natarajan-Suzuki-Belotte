package com.CS7319.Group06.eventual.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Unified request class for user registration and profile update.
 * All fields are optional at the class level; required-field rules
 * are enforced in the service layer depending on the operation.
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

    @Pattern(regexp = "ORGANIZER|PARTICIPANT",
             message = "Role must be ORGANIZER or PARTICIPANT")
    private String role;

    @Size(max = 256)
    private String location;

    @Size(max = 1000)
    private String aboutMe;

    @Size(max = 5, message = "You may select at most 5 categories")
    private List<Integer> categoryIds;

    private List<Integer> groupIds;
}
