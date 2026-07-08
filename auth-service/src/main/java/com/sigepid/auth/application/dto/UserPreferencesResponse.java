package com.sigepid.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponse {
    
    private Long userId;
    private List<String> preferredCategories;
    private String ageRange;
}
