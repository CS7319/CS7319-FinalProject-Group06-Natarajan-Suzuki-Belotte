package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.config.FileStorageConfig;
import com.CS7319.Group06.eventual.dao.UserDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.AuthResponse;
import com.CS7319.Group06.eventual.model.User;
import com.CS7319.Group06.eventual.model.UserCredentials;
import com.CS7319.Group06.eventual.model.UserRequest;
import com.CS7319.Group06.eventual.service.UserService;
import com.CS7319.Group06.eventual.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementation for UserService
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FileStorageConfig fileStorageConfig;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                           FileStorageConfig fileStorageConfig) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.fileStorageConfig = fileStorageConfig;
    }

    @Override
    public User register(UserRequest request, MultipartFile profilePicture) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (request.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (userDao.getUserByEmail(request.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPronoun(request.getPronoun());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLocation(request.getLocation());
        user.setAboutMe(request.getAboutMe());
        user.setCategoryTypes(request.getCategoryTypes());
        user.setGroupIds(request.getGroupIds());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            user.setProfilePicturePath(storeProfilePicture(request.getEmail(), profilePicture));
        }

        try {
            return userDao.createProfile(user);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public AuthResponse login(UserCredentials credentials) {
        User user = userDao.getUserByEmail(credentials.getEmail());

        if (user == null || !passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    @Override
    public User update(String email, UserRequest request, MultipartFile profilePicture) {
        if (userDao.getUserByEmail(email) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        User updates = new User();
        updates.setEmail(email);
        updates.setPronoun(request.getPronoun());
        updates.setLocation(request.getLocation());
        updates.setAboutMe(request.getAboutMe());
        updates.setRole(request.getRole());
        updates.setPasswordHash(request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null);
        updates.setCategoryTypes(request.getCategoryTypes());
        updates.setGroupIds(request.getGroupIds());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            updates.setProfilePicturePath(storeProfilePicture(email, profilePicture));
        }

        try {
            userDao.updateProfile(updates);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return userDao.getUserByEmail(email);
    }

    private String storeProfilePicture(String email, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed for profile picture");
        }
        try {
            Path userDir = Paths.get(fileStorageConfig.getUploadDir(), "profile-pictures", email);
            Files.createDirectories(userDir);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), userDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "profile-pictures/" + email + "/" + filename;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store profile picture");
        }
    }
}
