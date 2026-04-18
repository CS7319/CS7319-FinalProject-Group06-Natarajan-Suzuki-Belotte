package com.CS7319.Group06.eventual.userservice.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * UserCredentials - user credentials.
 */
@Data
public class UserCredentials {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
