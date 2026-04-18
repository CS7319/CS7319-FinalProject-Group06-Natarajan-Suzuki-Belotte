package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.GroupJoinRequest;
import com.CS7319.Group06.eventual.model.GroupRequest;
import com.CS7319.Group06.eventual.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages group operations — create, edit, join, and join request approvals
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public Group createGroup(@Valid @RequestBody GroupRequest request, Authentication authentication) {
        return groupService.createGroup(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public Group updateGroup(@PathVariable int id, @Valid @RequestBody GroupRequest request, Authentication authentication) {
        return groupService.updateGroup(id, request, authentication.getName());
    }

    /**
     * Join a group. public groups allows you to join them immediately. private group you have to request to join
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGroup(@PathVariable int id, Authentication authentication) {
        String userEmail = authentication.getName();
        if (Boolean.TRUE.equals(groupService.getGroupById(id).getIsPublic())) {
            return ResponseEntity.ok(groupService.joinPublicGroup(id, userEmail));
        }
        return ResponseEntity.accepted().body(groupService.requestToJoinGroup(id, userEmail));
    }

    /**
     * Get all PENDING join requests for a group — group owner only.
     */
    @GetMapping("/{id}/join-requests")
    @PreAuthorize("hasRole('ORGANIZER')")
    public List<GroupJoinRequest> getPendingRequests(@PathVariable int id, Authentication authentication) {
        return groupService.getPendingRequests(id, authentication.getName());
    }

    /**
     * Approve a join request — group owner only. Adds the requester to the group's members.
     */
    @PostMapping("/{id}/join-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ORGANIZER')")
    public GroupJoinRequest approveRequest(@PathVariable int id, @PathVariable int requestId, Authentication authentication) {
        return groupService.approveJoinRequest(id, requestId, authentication.getName());
    }

    /**
     * Reject a join request — group owner only.
     */
    @PostMapping("/{id}/join-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ORGANIZER')")
    public GroupJoinRequest rejectRequest(@PathVariable int id, @PathVariable int requestId, Authentication authentication) {
        return groupService.rejectJoinRequest(id, requestId, authentication.getName());
    }
}
