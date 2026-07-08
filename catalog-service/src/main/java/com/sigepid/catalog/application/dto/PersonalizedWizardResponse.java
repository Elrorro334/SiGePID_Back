package com.sigepid.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizedWizardResponse {
    private String ageRange;
    private List<String> preferredCategories;
    private List<WizardResponse> recommendations;
}
