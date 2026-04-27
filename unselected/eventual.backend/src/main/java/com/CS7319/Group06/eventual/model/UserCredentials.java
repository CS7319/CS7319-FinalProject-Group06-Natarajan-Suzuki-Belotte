package com.CS7319.Group06.eventual.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Request class to allow user to login
 */
@Data
public class UserCredentials {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
