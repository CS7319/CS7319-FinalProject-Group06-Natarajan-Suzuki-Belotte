package com.CS7319.Group06.eventual.searchservice.client;

import com.CS7319.Group06.eventual.searchservice.client.dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for communicating with the Event Service.
 */
@Slf4j
@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(@Value("${event.service.url}") String eventServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(eventServiceUrl).build();
    }

    /**
     * Returns list of event IDs the user already RSVPed to.
     *
     * @param email user email
     * @return list of event IDs
     */
    public List<Integer> getRsvpEventIdsByUser(String email) {
        try {
            return restClient.get()
                    .uri("/api/events/rsvps/by-user/{email}", email)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Integer>>() {});
        } catch (Exception e) {
            log.warn("Failed to fetch RSVPs for user {} from Event Service: {}", email, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns a page of events from event-service for reindexing.
     *
     * @param page zero-based page number
     * @param size page size
     * @return map with keys: events (List), total (int), page (int), size (int)
     */
    public Map<String, Object> getEventsPaginated(int page, int size) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/events/all")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to fetch events page {}/{} from Event Service: {}", page, size, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
