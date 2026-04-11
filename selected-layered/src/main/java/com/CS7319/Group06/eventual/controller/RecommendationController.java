package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.search.BaseSearchRequest;
import com.CS7319.Group06.eventual.model.search.EventDocument;
import com.CS7319.Group06.eventual.model.search.GroupDocument;
import com.CS7319.Group06.eventual.model.search.SearchResult;
import com.CS7319.Group06.eventual.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recommendations based on the users profile
 *
 * @author harininatarajan
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/events")
    public ResponseEntity<SearchResult<EventDocument>> recommendEvents(@ModelAttribute BaseSearchRequest request,
                                                                       Authentication authentication) {
        return ResponseEntity.ok(
                recommendationService.recommendEvents(authentication.getName(), request.getPage(), request.getSize()));
    }

    @GetMapping("/groups")
    public ResponseEntity<SearchResult<GroupDocument>> recommendGroups(@ModelAttribute BaseSearchRequest request,
                                                                       Authentication authentication) {
        return ResponseEntity.ok(
                recommendationService.recommendGroups(authentication.getName(), request.getPage(), request.getSize()));
    }
}
