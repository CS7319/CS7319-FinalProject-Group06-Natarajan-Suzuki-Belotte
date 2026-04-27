package com.CS7319.Group06.eventual.userservice.dao;

import com.CS7319.Group06.eventual.userservice.model.Group;

import java.util.List;

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
