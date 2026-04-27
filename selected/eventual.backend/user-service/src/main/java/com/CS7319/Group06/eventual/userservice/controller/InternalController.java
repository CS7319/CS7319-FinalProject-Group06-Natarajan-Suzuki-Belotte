package com.CS7319.Group06.eventual.userservice.controller;

import com.CS7319.Group06.eventual.userservice.dao.GroupDao;
import com.CS7319.Group06.eventual.userservice.dao.UserDao;
import com.CS7319.Group06.eventual.userservice.model.Group;
import com.CS7319.Group06.eventual.userservice.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Internal endpoints called directly by other microservices.
 * No auth headers are required — these should only be reachable within the cluster.
 */
@RestController
@RequestMapping("/api/internal")
public class InternalController {

    private final UserDao userDao;
    private final GroupDao groupDao;

    public InternalController(UserDao userDao, GroupDao groupDao) {
        this.userDao = userDao;
        this.groupDao = groupDao;
    }

    @GetMapping("/users/{email}")
    public User getUserByEmail(@PathVariable String email) {
        User user = userDao.getUserByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email);
        }
        return user;
    }

    @GetMapping("/groups/{id}")
    public Group getGroupById(@PathVariable int id) {
        Group group = groupDao.getGroupById(id);
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id);
        }
        return group;
    }
}
