package com.CS7319.Group06.eventual.model;

import lombok.Data;

/**
 * Represents a category (e.g. Music, Technology) that users and events can be tagged with
 *
 * @author harininatarajan
 */
@Data
public class Category {

    private int categoryId;
    private String name;
    private String type;
}
