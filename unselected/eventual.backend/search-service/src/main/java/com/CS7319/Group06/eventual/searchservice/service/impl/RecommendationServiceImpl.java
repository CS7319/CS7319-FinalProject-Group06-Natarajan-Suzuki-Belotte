package com.CS7319.Group06.eventual.searchservice.service.impl;

import com.CS7319.Group06.eventual.searchservice.client.EventServiceClient;
import com.CS7319.Group06.eventual.searchservice.client.UserServiceClient;
import com.CS7319.Group06.eventual.searchservice.client.dto.UserDto;
import com.CS7319.Group06.eventual.searchservice.dao.RecommendationDao;
import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.EventRecommendationContext;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupRecommendationContext;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;
import com.CS7319.Group06.eventual.searchservice.service.EmbeddingService;
import com.CS7319.Group06.eventual.searchservice.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

/**
 * Implementation for RecommendationService.
 * <p>
 * For each user recommendation request:
 * 1. Load the user's profile (interests, location, group memberships)
 * 2. Load their RSVP history to exclude events they already joined
 * 3. Build an interest text from their category types + location
 * 4. Generate a semantic embedding from that text via Ollama
 * 5. Pass everything to the RecommendationDao which runs the hybrid ES query
 */
@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationDao recommendationDao;
    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final EmbeddingService embeddingService;

    public RecommendationServiceImpl(RecommendationDao recommendationDao,
                                     UserServiceClient userServiceClient,
                                     EventServiceClient eventServiceClient,
                                     EmbeddingService embeddingService) {
        this.recommendationDao = recommendationDao;
        this.userServiceClient = userServiceClient;
        this.eventServiceClient = eventServiceClient;
        this.embeddingService = embeddingService;
    }

    @Override
    public SearchResult<EventDocument> recommendEvents(String userEmail, int page, int size) {
        UserDto user = userServiceClient.getUserByEmail(userEmail);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        // Get event IDs the user already RSVPed to
        List<String> excludeEventIds = eventServiceClient.getRsvpEventIdsByUser(userEmail)
                .stream().map(String::valueOf).toList();

        float[] embedding = generateInterestEmbedding(user.getCategoryTypes(), user.getLocation());

        EventRecommendationContext context = EventRecommendationContext.builder()
                .categoryTypes(user.getCategoryTypes())
                .location(user.getLocation())
                .excludeEventIds(excludeEventIds)
                .embedding(embedding)
                .page(page)
                .size(size)
                .build();

        return recommendationDao.recommendEvents(context);
    }

    @Override
    public SearchResult<GroupDocument> recommendGroups(String userEmail, int page, int size) {
        UserDto user = userServiceClient.getUserByEmail(userEmail);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<String> excludeGroupIds = user.getGroupIds() != null
                ? user.getGroupIds().stream().map(String::valueOf).toList()
                : Collections.emptyList();

        float[] embedding = generateInterestEmbedding(user.getCategoryTypes(), user.getLocation());

        GroupRecommendationContext context = GroupRecommendationContext.builder()
                .categoryTypes(user.getCategoryTypes())
                .excludeGroupIds(excludeGroupIds)
                .embedding(embedding)
                .page(page)
                .size(size)
                .build();

        return recommendationDao.recommendGroups(context);
    }

    private float[] generateInterestEmbedding(List<String> categories, String location) {
        StringBuilder interestText = new StringBuilder();
        if (categories != null && !categories.isEmpty()) {
            interestText.append(String.join(" ", categories));
        }
        if (location != null && !location.isBlank()) {
            if (!interestText.isEmpty()) interestText.append(" ");
            interestText.append(location);
        }
        if (interestText.isEmpty()) return null;
        try {
            return embeddingService.generateEmbedding(interestText.toString());
        } catch (Exception e) {
            log.warn("Embedding failed, falling back to filter-only: {}", e.getMessage());
            return null;
        }
    }
}
