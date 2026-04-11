package com.CS7319.Group06.eventual.model.search;

import lombok.Data;

/**
 * Base class for search requests containing common pagination fields.
 *
 * @author harininatarajan
 */
@Data
public class BaseSearchRequest {

    private int page = 0; //page number used for pagination

    private int size = 10; //results to return per page
}
