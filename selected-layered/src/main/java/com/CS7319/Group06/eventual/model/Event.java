package com.CS7319.Group06.eventual.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an event in the platform
 *
 * @author harininatarajan
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

    private String organizerEmail;

    private String organizerName; //To display the name in the UI

    @Min(0)
    private int capacity;

    private String eventPicture;

    @NotNull
    @Pattern(regexp = "PUBLIC|GROUP", message = "Event type must be PUBLIC or GROUP")
    private String eventType;

    // Required when eventType is GROUP
    private Integer groupId;

    @Size(max = 3, message = "An event can have at most 3 categories")
    private List<Integer> categoryIds;
}
