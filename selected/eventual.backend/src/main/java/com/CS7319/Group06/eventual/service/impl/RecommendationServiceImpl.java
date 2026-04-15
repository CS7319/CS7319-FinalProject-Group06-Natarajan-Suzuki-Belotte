package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.RecommendationDao;
import com.CS7319.Group06.eventual.dao.RsvpDao;
import com.CS7319.Group06.eventual.dao.UserDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Rsvp;
import com.CS7319.Group06.eventual.model.User;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.EventRecommendationContext;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.GroupRecommendationContext;
import com.CS7319.Group06.eventual.model.search.SearchResult;
import com.CS7319.Group06.eventual.service.EmbeddingService;
import com.CS7319.Group06.eventual.service.RecommendationService;
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
 *
 * @author harininatarajan
 */
@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationDao recommendationDao;
    private final UserDao userDao;
    private final RsvpDao rsvpDao;
    private final EmbeddingService embeddingService;

    public RecommendationServiceImpl(RecommendationDao recommendationDao,
                                     UserDao userDao,
                                     RsvpDao rsvpDao,
                                     EmbeddingService embeddingService) {
        this.recommendationDao = recommendationDao;
        this.userDao = userDao;
        this.rsvpDao = rsvpDao;
        this.embeddingService = embeddingService;
    }

    @Override
    public SearchResult<EventDocument> recommendEvents(String userEmail, int page, int size) {
        User user = getUser(userEmail);

        // Events the user already RSVPed to — exclude from recommendations
        List<String> excludeEventIds = getExcludeEventIds(userEmail);

        // Build embedding from user's interests + location
        float[] embedding = generateInterestEmbedding(user);

        EventRecommendationContext context = EventRecommendationContext.builder()
                .categoryTypes(user.getCategoryTypes())
                .location(user.getLocation())
                .excludeEventIds(excludeEventIds)
                .embedding(embedding)
                .page(page)
                .size(size)
                .build();

        try {
            return recommendationDao.recommendEvents(context);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public SearchResult<GroupDocument> recommendGroups(String userEmail, int page, int size) {
        User user = getUser(userEmail);

        // Groups the user is already a member of — exclude from recommendations
        List<String> excludeGroupIds = user.getGroupIds() != null
                ? user.getGroupIds().stream().map(String::valueOf).toList()
                : Collections.emptyList();

        float[] embedding = generateInterestEmbedding(user);

        GroupRecommendationContext context = GroupRecommendationContext.builder()
                .categoryTypes(user.getCategoryTypes())
                .excludeGroupIds(excludeGroupIds)
                .embedding(embedding)
                .page(page)
                .size(size)
                .build();

        try {
            return recommendationDao.recommendGroups(context);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private User getUser(String userEmail) {
        User user = userDao.getUserByEmail(userEmail);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    private List<String> getExcludeEventIds(String userEmail) {
        try {
            return rsvpDao.getRsvpsByUser(userEmail).stream()
                    .map(Rsvp::getEventId)
                    .map(String::valueOf)
                    .toList();
        } catch (DaoException e) {
            log.warn("Could not load RSVP history for {}: {}", userEmail, e.getMessage());
            return Collections.emptyList();
        }
    }

    //Builds a plain-text representation of the users interests and generates an embedding from it
    private float[] generateInterestEmbedding(User user) {
        StringBuilder interestText = new StringBuilder();

        if (user.getCategoryTypes() != null && !user.getCategoryTypes().isEmpty()) {
            interestText.append(String.join(" ", user.getCategoryTypes()));
        }
        if (user.getLocation() != null && !user.getLocation().isBlank()) {
            if (!interestText.isEmpty()) interestText.append(" ");
            interestText.append(user.getLocation());
        }

        if (interestText.isEmpty()) {
            return null;
        }

        try {
            return embeddingService.generateEmbedding(interestText.toString());
        } catch (Exception e) {
            log.warn("Could not generate embedding for recommendations, falling back to filter-only: {}", e.getMessage());
            return null;
        }
    }
}
