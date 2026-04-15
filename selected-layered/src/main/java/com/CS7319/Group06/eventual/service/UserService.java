package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.AuthResponse;
import com.CS7319.Group06.eventual.model.User;
import com.CS7319.Group06.eventual.model.UserCredentials;
import com.CS7319.Group06.eventual.model.UserRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for user operations
 *
 * @author harininatarajan
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
