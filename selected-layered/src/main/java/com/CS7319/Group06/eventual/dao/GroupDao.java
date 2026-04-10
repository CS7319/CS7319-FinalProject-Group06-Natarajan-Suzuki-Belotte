package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Group;

import java.util.List;

/**
 * Data layer for groups
 *
 * @author harininatarajan
 */
public interface GroupDao {

    List<Group> getAllGroups();

    Group getGroupById(int id);

    Group createGroup(Group group);

    Group updateGroup(Group group);
}
