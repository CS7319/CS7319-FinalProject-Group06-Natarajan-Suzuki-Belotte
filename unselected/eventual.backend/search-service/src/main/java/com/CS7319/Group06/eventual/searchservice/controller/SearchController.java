package com.CS7319.Group06.eventual.searchservice.controller;

import com.CS7319.Group06.eventual.searchservice.model.EventDocument;
import com.CS7319.Group06.eventual.searchservice.model.EventSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.GroupDocument;
import com.CS7319.Group06.eventual.searchservice.model.GroupSearchRequest;
import com.CS7319.Group06.eventual.searchservice.model.SearchResult;
import com.CS7319.Group06.eventual.searchservice.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Search Controller to search for events and groups
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/events")
    public ResponseEntity<SearchResult<EventDocument>> searchEvents(@ModelAttribute EventSearchRequest request) {
        return ResponseEntity.ok(searchService.searchEvents(request));
    }

    @GetMapping("/groups")
    public ResponseEntity<SearchResult<GroupDocument>> searchGroups(@ModelAttribute GroupSearchRequest request) {
        return ResponseEntity.ok(searchService.searchGroups(request));
    }
}
