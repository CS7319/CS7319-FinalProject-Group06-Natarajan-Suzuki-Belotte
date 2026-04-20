package com.CS7319.Group06.eventual.userservice.service;

import com.CS7319.Group06.eventual.userservice.model.Group;
import com.CS7319.Group06.eventual.userservice.model.GroupJoinRequest;
import com.CS7319.Group06.eventual.userservice.model.GroupRequest;

import java.util.List;
import java.util.Map;

/**
 * Service to manage groups
 */
public interface GroupService {

    /**
     * Get group by id
     *
     * @param id
     * @return
     */
    Group getGroupById(int id);

    /**
     * Create a new group
     *
     * @param request
     * @param creatorEmail
     * @return
     */
    Group createGroup(GroupRequest request, String creatorEmail);

    /**
     * Update group
     *
     * @param id
     * @param request
     * @param requesterEmail
     * @return
     */
    Group updateGroup(int id, GroupRequest request, String requesterEmail);

    /**
     * Adding members to public group
     *
     * @param groupId
     * @param userEmail
     * @return
     */
    Group joinPublicGroup(int groupId, String userEmail);

    /**
     * For private group you request to join
     *
     * @param groupId
     * @param userEmail
     * @return
     */
    GroupJoinRequest requestToJoinGroup(int groupId, String userEmail);

    /**
     * This is for the organizer to see all pending request
     *
     * @param groupId
     * @param requesterEmail
     * @return
     */
    List<GroupJoinRequest> getPendingRequests(int groupId, String requesterEmail);

    /**
     * Organizer approves the request to join the group
     *
     * @param groupId
     * @param requestId
     * @param ownerEmail
     * @return
     */
    GroupJoinRequest approveJoinRequest(int groupId, int requestId, String ownerEmail);

    /**
     * Organizer rejects the request to join the group
     *
     * @param groupId
     * @param requestId
     * @param ownerEmail
     * @return
     */
    GroupJoinRequest rejectJoinRequest(int groupId, int requestId, String ownerEmail);

    /**
     * Get all groups paginated — used by the search-service reindex endpoint.
     *
     * @param page zero-based page number
     * @param size page size
     * @return list of groups
     */
    List<Group> getGroupsPaginated(int page, int size);

    /**
     * Count total number of groups.
     *
     * @return total count
     */
    int countGroups();
}
