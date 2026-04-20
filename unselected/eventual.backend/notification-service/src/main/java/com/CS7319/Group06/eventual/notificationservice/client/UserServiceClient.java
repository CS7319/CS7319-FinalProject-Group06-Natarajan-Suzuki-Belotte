package com.CS7319.Group06.eventual.notificationservice.client;

import com.CS7319.Group06.eventual.notificationservice.client.dto.GroupDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * HTTP client for fetching group data from the User Service.
 * Used by the Notification Service to resolve group members when notifying about new group events.
 */
@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${user.service.url}") String userServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(userServiceUrl).build();
    }

    /**
     * Fetches a group by ID from the User Service.
     * Returns null if the group is not found or the call fails.
     */
    public GroupDto getGroupById(int groupId) {
        try {
            return restClient.get()
                    .uri("/api/internal/groups/{id}", groupId)
                    .retrieve()
                    .body(GroupDto.class);
        } catch (Exception e) {
            log.warn("Failed to fetch group {} from User Service: {}", groupId, e.getMessage());
            return null;
        }
    }
}
