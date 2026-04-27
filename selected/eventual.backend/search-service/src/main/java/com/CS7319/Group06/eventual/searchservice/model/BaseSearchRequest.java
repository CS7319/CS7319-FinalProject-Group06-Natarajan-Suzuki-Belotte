package com.CS7319.Group06.eventual.searchservice.model;

import lombok.Data;

/**
 * Base class for search requests containing common pagination fields.
 */
@Data
public class BaseSearchRequest {

    private int page = 0; //page number used for pagination

    private int size = 10; //results to return per page
}
