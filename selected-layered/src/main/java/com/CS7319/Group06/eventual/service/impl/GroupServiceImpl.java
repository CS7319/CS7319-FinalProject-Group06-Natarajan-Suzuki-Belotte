package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.GroupDao;
import com.CS7319.Group06.eventual.dao.GroupJoinRequestDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.GroupJoinRequest;
import com.CS7319.Group06.eventual.model.GroupRequest;
import com.CS7319.Group06.eventual.model.constants.JoinRequestStatus;
import com.CS7319.Group06.eventual.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for GroupService
 *
 * @author harininatarajan
 */
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;
    private final GroupJoinRequestDao joinRequestDao;

    public GroupServiceImpl(GroupDao groupDao, GroupJoinRequestDao joinRequestDao) {
        this.groupDao = groupDao;
        this.joinRequestDao = joinRequestDao;
    }

    @Override
    public Group createGroup(GroupRequest request, String creatorEmail) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group description is required");
        }

        List<String> initialMembers = new ArrayList<>();
        initialMembers.add(creatorEmail);
        if (request.getMemberEmails() != null) {
            request.getMemberEmails().stream()
                    .filter(e -> !e.equals(creatorEmail))
                    .forEach(initialMembers::add);
        }

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatorEmail(creatorEmail);
        group.setOwnerEmail(creatorEmail);
        group.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        group.setMemberEmails(initialMembers);

        try {
            return groupDao.createGroup(group);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Group updateGroup(int id, GroupRequest request, String requesterEmail) {
        Group existing = getGroup(id);
        if (!existing.getOwnerEmail().equals(requesterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can edit this group");
        }

        Group updates = new Group();
        updates.setGroupId(id);
        updates.setName(request.getName());
        updates.setDescription(request.getDescription());
        updates.setOwnerEmail(request.getOwnerEmail());
        updates.setIsPublic(request.getIsPublic());
        updates.setMemberEmails(request.getMemberEmails());
        updates.setModifiedBy(requesterEmail);

        try {
            return groupDao.updateGroup(updates);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Group getGroupById(int id) {
        return getGroup(id);
    }

    @Override
    public Group joinPublicGroup(int groupId, String userEmail) {
        Group group = getGroup(groupId);
        if (group.getMemberEmails() != null && group.getMemberEmails().contains(userEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already a member of this group");
        }
        try {
            return groupDao.addMember(groupId, userEmail);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public GroupJoinRequest requestToJoinGroup(int groupId, String userEmail) {
        Group group = getGroup(groupId);
        if (group.getMemberEmails() != null && group.getMemberEmails().contains(userEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already a member of this group");
        }
        GroupJoinRequest existing = joinRequestDao.getRequestByGroupAndUser(groupId, userEmail);
        if (existing != null && JoinRequestStatus.PENDING == existing.getStatus()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a pending join request for this group");
        }
        try {
            if (existing != null) {
                // Previously rejected — re-submit
                return joinRequestDao.updateRequestStatus(existing.getId(), JoinRequestStatus.PENDING);
            }
            GroupJoinRequest joinRequest = new GroupJoinRequest();
            joinRequest.setGroupId(groupId);
            joinRequest.setRequesterEmail(userEmail);
            joinRequest.setStatus(JoinRequestStatus.PENDING);
            return joinRequestDao.createRequest(joinRequest);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<GroupJoinRequest> getPendingRequests(int groupId, String requesterEmail) {
        Group group = getGroup(groupId);
        if (!group.getOwnerEmail().equals(requesterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can view join requests");
        }
        try {
            return joinRequestDao.getPendingRequestsByGroup(groupId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public GroupJoinRequest approveJoinRequest(int groupId, int requestId, String ownerEmail) {
        Group group = getGroup(groupId);
        if (!group.getOwnerEmail().equals(ownerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can approve join requests");
        }

        GroupJoinRequest request = getGroupJoinRequest(requestId, groupId);
        if (JoinRequestStatus.PENDING != request.getStatus()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Request is already " + request.getStatus().name().toLowerCase());
        }

        try {
            groupDao.addMember(groupId, request.getRequesterEmail());
            return joinRequestDao.updateRequestStatus(requestId, JoinRequestStatus.APPROVED);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public GroupJoinRequest rejectJoinRequest(int groupId, int requestId, String ownerEmail) {
        Group group = getGroup(groupId);
        if (!group.getOwnerEmail().equals(ownerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can reject join requests");
        }

        GroupJoinRequest request = getGroupJoinRequest(requestId, groupId);
        if (JoinRequestStatus.PENDING != request.getStatus()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Request is already " + request.getStatus().name().toLowerCase());
        }

        try {
            return joinRequestDao.updateRequestStatus(requestId, JoinRequestStatus.REJECTED);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Group getGroup(int groupId) {
        try {
            Group group = groupDao.getGroupById(groupId);
            if (group == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found with id: " + groupId);
            }
            return group;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private GroupJoinRequest getGroupJoinRequest(int requestId, int groupId) {
        try {
            GroupJoinRequest request = joinRequestDao.getRequestById(requestId);
            if (request == null || request.getGroupId() != groupId) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Join request not found");
            }
            return request;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
