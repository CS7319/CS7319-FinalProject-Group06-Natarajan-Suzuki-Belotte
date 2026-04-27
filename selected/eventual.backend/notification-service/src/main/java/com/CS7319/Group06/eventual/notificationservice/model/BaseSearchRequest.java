package com.CS7319.Group06.eventual.notificationservice.model;

import lombok.Data;

/**
 * BaseSearchRequest - base search request.
 */
@Data
public class BaseSearchRequest {
    private int page = 0;
    private int size = 20;
}
