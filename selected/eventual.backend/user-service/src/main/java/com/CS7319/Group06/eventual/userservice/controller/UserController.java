package com.CS7319.Group06.eventual.userservice.controller;

import com.CS7319.Group06.eventual.userservice.model.AuthResponse;
import com.CS7319.Group06.eventual.userservice.model.User;
import com.CS7319.Group06.eventual.userservice.model.UserCredentials;
import com.CS7319.Group06.eventual.userservice.model.UserRequest;
import com.CS7319.Group06.eventual.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * User Controller
 * Handles registration, login, and profile updates
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public User register(@Valid @ModelAttribute UserRequest request,
                         @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
        return userService.register(request, profilePicture);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody UserCredentials credentials) {
        return userService.login(credentials);
    }

    @PutMapping(value = "/{email}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public User update(@PathVariable String email,
                       @Valid @ModelAttribute UserRequest request,
                       @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
                       @RequestHeader(value = "X-Authenticated-User", required = false) String currentUser) {
        return userService.update(email, request, profilePicture);
    }
}
