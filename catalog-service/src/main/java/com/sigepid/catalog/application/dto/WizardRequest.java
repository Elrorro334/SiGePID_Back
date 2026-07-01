package com.sigepid.catalog.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WizardRequest {

    @NotBlank(message = "La categoría es requerida")
    private String categoria;

    @NotBlank(message = "El rango de edad es requerido")
    private String rangoEdad;

    @NotBlank(message = "El uso previsto es requerido")
    private String usoPrevisto;
}
