package com.CS7319.Group06.eventual.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * Elasticsearch document representing an indexed group.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupDocument {

    private String id; //ES document id

    private String name;

    private String description;

    private String ownerEmail;

    private Boolean isPublic;

    private int memberCount;

    //For semantic search
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private float[] embedding;
}
