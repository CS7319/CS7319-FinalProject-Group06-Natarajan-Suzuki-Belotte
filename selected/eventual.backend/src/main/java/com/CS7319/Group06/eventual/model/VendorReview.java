package com.CS7319.Group06.eventual.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a review/comment left on a vendor by an attendee or organizer.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VendorReview {

    private int reviewId;
    private int vendorId;
    private String reviewerEmail;
    private String reviewerName;

    @NotEmpty
    private String comment;

    @Min(1) @Max(5)
    private Integer rating;

    private LocalDateTime createdAt;
}
