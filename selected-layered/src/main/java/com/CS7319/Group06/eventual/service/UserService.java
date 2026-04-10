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

    User register(UserRequest request, MultipartFile profilePicture);

    AuthResponse login(UserCredentials credentials);

    User update(String email, UserRequest request, MultipartFile profilePicture);
}
