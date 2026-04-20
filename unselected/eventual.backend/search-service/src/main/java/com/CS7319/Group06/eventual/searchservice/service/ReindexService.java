package com.CS7319.Group06.eventual.searchservice.service;

import com.CS7319.Group06.eventual.searchservice.model.ReindexStatus;

/**
 * Service for triggering and monitoring a full Elasticsearch reindex from PostgreSQL data.
 */
public interface ReindexService {

    /**
     * Start a full reindex of all events and groups.
     * Returns immediately — the reindex runs in the background.
     * Throws if a reindex is already in progress.
     */
    void startReindex();

    /**
     * Get the current reindex status.
     *
     * @return current status snapshot
     */
    ReindexStatus getStatus();
}
