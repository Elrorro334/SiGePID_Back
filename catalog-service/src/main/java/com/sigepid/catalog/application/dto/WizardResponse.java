package com.sigepid.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WizardResponse {

    private String productoRecomendado;
    private String descripcion;
    private BigDecimal precioPromedio;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private Integer stockPromedio;
    private String categoriaPredominante;
    private Double confianza;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WizardOptions {
        private List<String> categorias;
        private List<String> rangosEdad;
        private List<String> usosPrevisto;
    }
}
