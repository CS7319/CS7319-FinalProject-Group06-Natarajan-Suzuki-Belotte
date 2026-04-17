package com.CS7319.Group06.eventual.model;

import com.CS7319.Group06.eventual.model.constants.EventType;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an event in the platform
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Event {

    private int eventId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private String location;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @JsonAlias("createdBy")
    private String organizerEmail;

    private String organizerName; //To display the name in the UI

    @Min(0)
    private int capacity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer availableSpots;

    private int waitlistCount;

    private String eventPicture;

    @NotNull
    private EventType eventType;

    // Required when eventType is GROUP
    private Integer groupId;

    @Size(max = 3, message = "An event can have at most 3 category types")
    private List<String> categoryTypes;

    private String modifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Vendors for the event
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Integer> vendorIds;
}
