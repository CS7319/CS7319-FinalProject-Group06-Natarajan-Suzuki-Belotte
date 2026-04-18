package com.CS7319.Group06.eventual.userservice.controller;

import com.CS7319.Group06.eventual.userservice.model.Group;
import com.CS7319.Group06.eventual.userservice.model.GroupJoinRequest;
import com.CS7319.Group06.eventual.userservice.model.GroupRequest;
import com.CS7319.Group06.eventual.userservice.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public Group createGroup(@Valid @RequestBody GroupRequest request,
                             @RequestHeader("X-Authenticated-User") String userEmail,
                             @RequestHeader("X-User-Role") String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can create groups");
        }
        return groupService.createGroup(request, userEmail);
    }

    @PutMapping("/{id}")
    public Group updateGroup(@PathVariable int id,
                             @Valid @RequestBody GroupRequest request,
                             @RequestHeader("X-Authenticated-User") String userEmail,
                             @RequestHeader("X-User-Role") String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can update groups");
        }
        return groupService.updateGroup(id, request, userEmail);
    }

    /**
     * Join a group. Public groups allow immediate join; private groups create a join request.
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGroup(@PathVariable int id,
                                       @RequestHeader("X-Authenticated-User") String userEmail) {
        if (Boolean.TRUE.equals(groupService.getGroupById(id).getIsPublic())) {
            return ResponseEntity.ok(groupService.joinPublicGroup(id, userEmail));
        }
        return ResponseEntity.accepted().body(groupService.requestToJoinGroup(id, userEmail));
    }

    /**
     * Get all PENDING join requests for a group — group owner only.
     */
    @GetMapping("/{id}/join-requests")
    public List<GroupJoinRequest> getPendingRequests(@PathVariable int id,
                                                     @RequestHeader("X-Authenticated-User") String userEmail,
                                                     @RequestHeader("X-User-Role") String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can view join requests");
        }
        return groupService.getPendingRequests(id, userEmail);
    }

    /**
     * Approve a join request — group owner only. Adds the requester to the group's members.
     */
    @PostMapping("/{id}/join-requests/{requestId}/approve")
    public GroupJoinRequest approveRequest(@PathVariable int id,
                                           @PathVariable int requestId,
                                           @RequestHeader("X-Authenticated-User") String userEmail,
                                           @RequestHeader("X-User-Role") String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can approve join requests");
        }
        return groupService.approveJoinRequest(id, requestId, userEmail);
    }

    /**
     * Reject a join request — group owner only.
     */
    @PostMapping("/{id}/join-requests/{requestId}/reject")
    public GroupJoinRequest rejectRequest(@PathVariable int id,
                                          @PathVariable int requestId,
                                          @RequestHeader("X-Authenticated-User") String userEmail,
                                          @RequestHeader("X-User-Role") String userRole) {
        if (!"ORGANIZER".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only organizers can reject join requests");
        }
        return groupService.rejectJoinRequest(id, requestId, userEmail);
    }
}
