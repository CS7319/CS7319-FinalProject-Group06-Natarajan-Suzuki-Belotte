package com.CS7319.Group06.eventual.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Unified request class for group creation and update.
 * All fields are optional at the class level; required-field rules
 * are enforced in the service layer depending on the operation.
 *
 * @author harininatarajan
 */
@Data
public class GroupRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean isPublic;

    private String ownerEmail;

    private List<String> memberEmails;
}
