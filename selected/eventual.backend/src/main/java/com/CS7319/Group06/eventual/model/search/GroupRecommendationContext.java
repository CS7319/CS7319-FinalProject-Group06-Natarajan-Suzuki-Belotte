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
public class GroupRecommendationContext {

    private List<String> categoryTypes; //users selected interest types for filter groups by category

    private List<String> excludeGroupIds; //Groups that user has already joined — excluded from results

    private float[] embedding; //Embedding generated from users interests for vector search

    private int page = 0; //page number used for pagination

    private int size = 10; //results to return per page

}
