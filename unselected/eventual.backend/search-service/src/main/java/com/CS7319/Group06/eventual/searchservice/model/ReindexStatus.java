package com.CS7319.Group06.eventual.searchservice.model;

import lombok.Data;

/**
 * Tracks the progress and outcome of an Elasticsearch reindex operation.
 */
@Data
public class ReindexStatus {

    public enum State { IDLE, RUNNING, COMPLETED, FAILED }

    private State state = State.IDLE;
    private int eventsIndexed;
    private int groupsIndexed;
    private int eventsFailed;
    private int groupsFailed;
    private Integer totalEvents;
    private Integer totalGroups;
    private String startedAt;
    private String completedAt;
    private String error;
}
