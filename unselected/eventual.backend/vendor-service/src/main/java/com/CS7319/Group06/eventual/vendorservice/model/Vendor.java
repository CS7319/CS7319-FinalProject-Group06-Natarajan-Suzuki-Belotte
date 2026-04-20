package com.CS7319.Group06.eventual.vendorservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a vendor that can be associated with events.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Vendor {

    private int vendorId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String serviceType;

    private String contactEmail;
    private String contactPhone;
    private String description;
    private String website;
    private boolean preferred;
    private String addedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VendorReview> reviews;
}
