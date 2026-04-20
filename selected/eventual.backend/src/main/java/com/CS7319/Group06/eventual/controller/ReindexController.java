package com.CS7319.Group06.eventual.controller;

import com.CS7319.Group06.eventual.model.search.ReindexStatus;
import com.CS7319.Group06.eventual.service.ReindexService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Triggers and monitors full Elasticsearch reindex operations.
 * Used to backfill the search index from existing PostgreSQL data.
 */
@RestController
@RequestMapping("/api/search/reindex")
public class ReindexController {

    private final ReindexService reindexService;

    public ReindexController(ReindexService reindexService) {
        this.reindexService = reindexService;
    }

    /**
     * Start a full reindex of all events and groups.
     */
    @PostMapping
    public ResponseEntity<ReindexStatus> startReindex() {
        reindexService.startReindex();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(reindexService.getStatus());
    }

    /**
     * Get the current reindex status.
     */
    @GetMapping("/status")
    public ResponseEntity<ReindexStatus> getStatus() {
        return ResponseEntity.ok(reindexService.getStatus());
    }
}
