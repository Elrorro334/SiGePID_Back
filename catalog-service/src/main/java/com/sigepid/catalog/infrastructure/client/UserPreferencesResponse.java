package com.sigepid.catalog.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponse {
    private Long userId;
    private List<String> preferredCategories;
    private String ageRange;
}
