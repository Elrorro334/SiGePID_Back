package com.sigepid.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesRequest {

    @NotEmpty(message = "At least one preferred category is required")
    private List<String> preferredCategories;

    @NotBlank(message = "Age range is required")
    private String ageRange;
}
