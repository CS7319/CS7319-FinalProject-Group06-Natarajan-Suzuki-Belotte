package com.CS7319.Group06.eventual.userservice.service;

import com.CS7319.Group06.eventual.userservice.model.AuthResponse;
import com.CS7319.Group06.eventual.userservice.model.User;
import com.CS7319.Group06.eventual.userservice.model.UserCredentials;
import com.CS7319.Group06.eventual.userservice.model.UserRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for user operations
 */
public interface UserService {

    /**
     * Register a new user
     *
     * @param request
     * @param profilePicture
     * @return
     */
    User register(UserRequest request, MultipartFile profilePicture);

    /**
     * User login
     *
     * @param credentials
     * @return
     */
    AuthResponse login(UserCredentials credentials);

    /**
     * Update user profile
     *
     * @param email
     * @param request
     * @param profilePicture
     * @return
     */
    User update(String email, UserRequest request, MultipartFile profilePicture);
}
