package com.CS7319.Group06.eventual.searchservice.controller;

import com.CS7319.Group06.eventual.searchservice.model.BaseSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;
import com.CS7319.Group06.eventual.searchservice.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recommendations based on the users profile
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/events")
    public ResponseEntity<SearchResult<EventDocument>> recommendEvents(
            @ModelAttribute BaseSearchRequest request,
            @RequestHeader("X-Authenticated-User") String userEmail) {
        return ResponseEntity.ok(
                recommendationService.recommendEvents(userEmail, request.getPage(), request.getSize()));
    }

    @GetMapping("/groups")
    public ResponseEntity<SearchResult<GroupDocument>> recommendGroups(
            @ModelAttribute BaseSearchRequest request,
            @RequestHeader("X-Authenticated-User") String userEmail) {
        return ResponseEntity.ok(
                recommendationService.recommendGroups(userEmail, request.getPage(), request.getSize()));
    }
}
