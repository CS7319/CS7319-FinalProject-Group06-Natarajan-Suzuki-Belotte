package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.GroupDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.GroupRequest;
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

    public GroupServiceImpl(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Override
    public List<Group> getAllGroups() {
        try {
            return groupDao.getAllGroups();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
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
        Group existing = groupDao.getGroupById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found with id: " + id);
        }
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

        try {
            return groupDao.updateGroup(updates);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
