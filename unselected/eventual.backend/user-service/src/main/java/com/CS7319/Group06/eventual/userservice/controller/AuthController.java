package com.CS7319.Group06.eventual.userservice.controller;

import com.CS7319.Group06.eventual.userservice.model.AuthResponse;
import com.CS7319.Group06.eventual.userservice.model.UserCredentials;
import com.CS7319.Group06.eventual.userservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login endpoint — open to all (no JWT required).
     * Returns a signed JWT token on successful authentication.
     *
     * TODO: Replace hardcoded credential check with a real user lookup via UserDao.
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody UserCredentials credentials) {
        String role = resolveRole(credentials.getUsername(), credentials.getPassword());
        String token = jwtUtil.generateToken(credentials.getUsername(), role);
        return new AuthResponse(token, credentials.getUsername(), role);
    }

    /**
     * TODO: Replace with actual database lookup once UserDao is implemented.
     * Hardcoded users for skeleton purposes:
     *   organizer / organizer123  → ORGANIZER
     *   participant / participant123 → PARTICIPANT
     *   moderator / moderator123  → MODERATOR
     */
    private String resolveRole(String username, String password) {
        return switch (username) {
            case "organizer"   -> validateAndReturn(password, "organizer123",   "ORGANIZER");
            case "participant" -> validateAndReturn(password, "participant123", "PARTICIPANT");
            case "moderator"   -> validateAndReturn(password, "moderator123",   "MODERATOR");
            default -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        };
    }

    private String validateAndReturn(String provided, String expected, String role) {
        if (!provided.equals(expected)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return role;
    }
}
