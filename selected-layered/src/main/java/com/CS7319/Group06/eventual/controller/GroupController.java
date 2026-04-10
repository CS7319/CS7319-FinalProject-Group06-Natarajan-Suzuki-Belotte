package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.GroupRequest;
import com.CS7319.Group06.eventual.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages group operations — browse, create, and edit groups
 *
 * @author harininatarajan
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public Group createGroup(@Valid @RequestBody GroupRequest request, Authentication authentication) {
        return groupService.createGroup(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public Group updateGroup(@PathVariable int id,
                             @Valid @RequestBody GroupRequest request,
                             Authentication authentication) {
        return groupService.updateGroup(id, request, authentication.getName());
    }
}
