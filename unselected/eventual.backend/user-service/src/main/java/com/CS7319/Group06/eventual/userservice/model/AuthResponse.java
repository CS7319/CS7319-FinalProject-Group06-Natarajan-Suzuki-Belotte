package com.CS7319.Group06.eventual.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AuthResponse - auth response.
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String role;
}
