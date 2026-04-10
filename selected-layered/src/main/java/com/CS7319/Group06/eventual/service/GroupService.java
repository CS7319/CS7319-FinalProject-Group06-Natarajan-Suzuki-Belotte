package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.Group;
import com.CS7319.Group06.eventual.model.GroupRequest;

import java.util.List;

/**
 * Service to manage groups
 *
 * @author harininatarajan
 */
public interface GroupService {

    List<Group> getAllGroups();

    Group createGroup(GroupRequest request, String creatorEmail);

    Group updateGroup(int id, GroupRequest request, String requesterEmail);
}
