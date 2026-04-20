package com.CS7319.Group06.eventual.searchservice.client.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * DTO for user profile received from User Service.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private String email;
    private String name;
    private String location;
    private List<String> categoryTypes;
    private List<Integer> groupIds;
}
