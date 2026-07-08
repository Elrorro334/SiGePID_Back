package com.sigepid.catalog.presentation.controller;

import com.sigepid.catalog.application.dto.WizardRequest;
import com.sigepid.catalog.application.dto.WizardResponse;
import com.sigepid.catalog.application.service.WizardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/wizard")
@RequiredArgsConstructor
public class WizardController {

    private final WizardService wizardService;

    /**
     * Recibe los parámetros del wizard y ejecuta el árbol de decisión ONNX.
     * Retorna el producto recomendado con estadísticas de precio y stock.
     */
    @PostMapping
    public ResponseEntity<WizardResponse> predict(@Valid @RequestBody WizardRequest request) {
        WizardResponse response = wizardService.predict(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna las opciones disponibles para cada pregunta del wizard,
     * permitiendo al frontend construir el cuestionario dinámicamente.
     */
    @GetMapping("/options")
    public ResponseEntity<WizardResponse.WizardOptions> getOptions() {
        return ResponseEntity.ok(wizardService.getOptions());
    }

    /**
     * Endpoint para generar recomendaciones personalizadas utilizando las preferencias
     * guardadas del usuario en el auth-service.
     */
    @GetMapping("/personalized")
    public ResponseEntity<com.sigepid.catalog.application.dto.PersonalizedWizardResponse> predictPersonalized(
            @org.springframework.web.bind.annotation.RequestHeader("X-User-Id") String userIdHeader,
            @org.springframework.web.bind.annotation.RequestParam String usoPrevisto) {
        
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        
        Long userId = Long.parseLong(userIdHeader);
        com.sigepid.catalog.application.dto.PersonalizedWizardResponse response = wizardService.predictPersonalized(userId, usoPrevisto);
        return ResponseEntity.ok(response);
    }
}
