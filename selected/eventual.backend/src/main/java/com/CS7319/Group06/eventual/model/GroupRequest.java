package com.CS7319.Group06.eventual.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request class for group creation and update.
 *
 * @author harininatarajan
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean isPublic;

    private String ownerEmail;

    private List<String> memberEmails;
}
