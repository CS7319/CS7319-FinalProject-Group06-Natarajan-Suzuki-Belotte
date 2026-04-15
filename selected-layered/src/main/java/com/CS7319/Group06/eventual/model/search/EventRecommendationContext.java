package com.CS7319.Group06.eventual.model.search;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * User context passed from RecommendationService to RecommendationDao.
 *
 * @author harininatarajan
 */
@Data
@Builder
public class EventRecommendationContext {

    private List<String> categoryTypes; //Users selected interest types — used to filter events by category

    private String location; //Users location — used to boost events in the same area

    private List<String> excludeEventIds; //Events the user has already RSVPed to — excluded from results

    private float[] embedding; //Embedding generated from users interests for vector search

    private int page = 0; //page number used for pagination

    private int size = 10; //results to return per page

}
