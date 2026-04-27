package com.CS7319.Group06.eventual.searchservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * Query parameters for searching groups.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GroupSearchRequest extends BaseSearchRequest {

    private String query; //free-text search across name and description

    private Boolean isPublic; //filtering
}
