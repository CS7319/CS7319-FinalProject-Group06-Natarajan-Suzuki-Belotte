package com.CS7319.Group06.eventual.eventservice.client;

import com.CS7319.Group06.eventual.eventservice.client.dto.GroupDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * UserServiceClient - client for user service communication.
 */
@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${user.service.url}") String userServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(userServiceUrl).build();
    }

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
