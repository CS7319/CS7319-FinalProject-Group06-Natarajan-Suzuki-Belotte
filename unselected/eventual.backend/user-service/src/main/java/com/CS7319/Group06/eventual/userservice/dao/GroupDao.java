package com.CS7319.Group06.eventual.userservice.dao;

import com.CS7319.Group06.eventual.userservice.model.Group;

/**
 * Data layer for groups
 */
public interface GroupDao {

    /**
     * Get by group id
     *
     * @param id
     * @return
     */
    Group getGroupById(int id);

    /**
     * Create a new group
     *
     * @param group
     * @return
     */
    Group createGroup(Group group);

    /**
     * Update group
     *
     * @param group
     * @return
     */
    Group updateGroup(Group group);

    /**
     * Add member to the group
     *
     * @param groupId
     * @param memberEmail
     * @return
     */
    Group addMember(int groupId, String memberEmail);
}
