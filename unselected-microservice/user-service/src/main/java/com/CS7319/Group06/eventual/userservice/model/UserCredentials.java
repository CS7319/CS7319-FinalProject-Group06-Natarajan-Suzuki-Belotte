package com.CS7319.Group06.eventual.userservice.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserCredentials {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
