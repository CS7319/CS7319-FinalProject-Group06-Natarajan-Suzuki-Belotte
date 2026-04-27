package com.CS7319.Group06.eventual.userservice.dao;

import com.CS7319.Group06.eventual.userservice.model.GroupJoinRequest;
import com.CS7319.Group06.eventual.userservice.model.constants.JoinRequestStatus;

import java.util.List;

/**
 * Data layer for group join requests
 */
public interface GroupJoinRequestDao {

    /**
     * Request to join the group by id
     *
     * @param id
     * @return
     */
    GroupJoinRequest getRequestById(int id);

    /**
     * Get a particular request by user to join the group
     *
     * @param groupId
     * @param requesterEmail
     * @return
     */
    GroupJoinRequest getRequestByGroupAndUser(int groupId, String requesterEmail);

    /**
     * For organizers to see all pending requests
     *
     * @param groupId
     * @return
     */
    List<GroupJoinRequest> getPendingRequestsByGroup(int groupId);

    /**
     * Create a new group joining request
     *
     * @param request
     * @return
     */
    GroupJoinRequest createRequest(GroupJoinRequest request);

    /**
     * Update the status
     *
     * @param id
     * @param status
     * @return
     */
    GroupJoinRequest updateRequestStatus(int id, JoinRequestStatus status);
}
