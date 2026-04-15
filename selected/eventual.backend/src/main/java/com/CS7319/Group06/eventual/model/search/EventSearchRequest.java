package com.CS7319.Group06.eventual.model.search;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * Query parameters for searching events.
 *
 * @author harininatarajan
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventSearchRequest extends BaseSearchRequest {

    private String query; //free-text to search across title & description

    private String location; //perform fuzzy match on location

    private String eventType; //exact match or filter

    private String startAfter; //events starting after a certain date

    private String startBefore; //events starting before a certain date

    private List<String> categoryTypes; //filtering
}
