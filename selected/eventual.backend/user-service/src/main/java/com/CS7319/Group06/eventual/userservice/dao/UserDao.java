package com.CS7319.Group06.eventual.userservice.dao;

import com.CS7319.Group06.eventual.userservice.model.User;

/**
 * Data access layer for user
 */
public interface UserDao {

    /**
     * Create user data layer
     *
     * @param user
     * @return
     */
    User createProfile(User user);

    /**
     * Get user by email
     *
     * @param email
     * @return
     */
    User getUserByEmail(String email);

    /**
     * Update the profile
     *
     * @param user
     */
    void updateProfile(User user);
}
