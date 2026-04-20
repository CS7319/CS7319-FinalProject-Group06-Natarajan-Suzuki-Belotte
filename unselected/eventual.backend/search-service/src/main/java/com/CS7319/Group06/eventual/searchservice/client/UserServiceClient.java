package com.CS7319.Group06.eventual.searchservice.client;

import com.CS7319.Group06.eventual.searchservice.client.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Map;

/**
 * HTTP client for communicating with the User Service.
 */
@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${user.service.url}") String userServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(userServiceUrl).build();
    }

    public UserDto getUserByEmail(String email) {
        try {
            return restClient.get()
                    .uri("/api/internal/users/{email}", email)
                    .retrieve()
                    .body(UserDto.class);
        } catch (Exception e) {
            log.warn("Failed to fetch user {} from User Service: {}", email, e.getMessage());
            return null;
        }
    }

    /**
     * Returns a page of groups from user-service for reindexing.
     *
     * @param page zero-based page number
     * @param size page size
     * @return map with keys: groups (List), total (int), page (int), size (int)
     */
    public Map<String, Object> getGroupsPaginated(int page, int size) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/groups/all")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to fetch groups page {}/{} from User Service: {}", page, size, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
