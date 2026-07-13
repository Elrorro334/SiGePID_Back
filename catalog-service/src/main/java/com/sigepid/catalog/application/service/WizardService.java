package com.sigepid.catalog.application.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigepid.catalog.application.dto.WizardRequest;
import com.sigepid.catalog.application.dto.WizardResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class WizardService {

    @Autowired
    private com.sigepid.catalog.infrastructure.client.AuthServiceClient authServiceClient;

    private OrtEnvironment ortEnv;
    private OrtSession ortSession;

    private Map<String, Integer> encoderCategoria;
    private Map<String, Integer> encoderRangoEdad;
    private Map<String, Integer> encoderUsoPrevisto;
    private Map<String, String>  encoderProducto;

    private Map<String, Map<String, Object>> productStats;

    private WizardResponse.WizardOptions wizardOptions;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        try {
            log.info("Iniciando WizardService: cargando modelo ONNX y encoders...");

            ortEnv = OrtEnvironment.getEnvironment();
            ClassPathResource modelResource = new ClassPathResource("ml/modelo_arbol_sigepid.onnx");
            byte[] modelBytes = modelResource.getInputStream().readAllBytes();
            ortSession = ortEnv.createSession(modelBytes, new OrtSession.SessionOptions());
            log.info("Modelo ONNX cargado. Inputs: {}, Outputs: {}",
                    ortSession.getInputNames(), ortSession.getOutputNames());

            ClassPathResource mappingsResource = new ClassPathResource("ml/encoder_mappings.json");
            Map<String, Object> mappingsFile = objectMapper.readValue(
                    mappingsResource.getInputStream(),
                    new TypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> encoders = (Map<String, Object>) mappingsFile.get("encoders");
            encoderCategoria   = castToStringIntMap((Map<String, Object>) encoders.get("categoria"));
            encoderRangoEdad   = castToStringIntMap((Map<String, Object>) encoders.get("rango_edad"));
            encoderUsoPrevisto = castToStringIntMap((Map<String, Object>) encoders.get("uso_previsto"));

            Map<String, Object> rawProducto = (Map<String, Object>) encoders.get("producto");
            encoderProducto = new java.util.HashMap<>();
            rawProducto.forEach((k, v) -> encoderProducto.put(k, (String) v));

            Map<String, Object> options = (Map<String, Object>) mappingsFile.get("options");
            wizardOptions = WizardResponse.WizardOptions.builder()
                    .categorias((List<String>) options.get("categorias"))
                    .rangosEdad((List<String>) options.get("rangosEdad"))
                    .usosPrevisto((List<String>) options.get("usosPrevisto"))
                    .build();

            ClassPathResource statsResource = new ClassPathResource("ml/product_stats.json");
            productStats = objectMapper.readValue(
                    statsResource.getInputStream(),
                    new TypeReference<Map<String, Map<String, Object>>>() {}
            );

            log.info("WizardService inicializado. {} productos, {} categorías, {} rangos de edad, {} usos",
                    encoderProducto.size(), encoderCategoria.size(),
                    encoderRangoEdad.size(), encoderUsoPrevisto.size());

        } catch (OrtException | IOException e) {
            log.error("Error al inicializar WizardService: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar el motor de ML del wizard", e);
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (ortSession != null) ortSession.close();
            if (ortEnv != null) ortEnv.close();
        } catch (OrtException e) {
            log.warn("Error al cerrar OrtSession: {}", e.getMessage());
        }
    }

    public WizardResponse predict(WizardRequest request) {
        try {

            int catNum  = encodeFeature(encoderCategoria, request.getCategoria(), "categoria");
            int edadNum = encodeFeature(encoderRangoEdad, request.getRangoEdad(), "rangoEdad");
            int usoNum  = encodeFeature(encoderUsoPrevisto, request.getUsoPrevisto(), "usoPrevisto");

            log.debug("Inputs codificados → categoria:{}, rangoEdad:{}, usoPrevisto:{}",
                    catNum, edadNum, usoNum);

            long[][] inputData = {{catNum, edadNum, usoNum}};
            OnnxTensor inputTensor = OnnxTensor.createTensor(ortEnv, inputData);

            String inputName = ortSession.getInputNames().iterator().next();
            OrtSession.Result result = ortSession.run(
                    java.util.Collections.singletonMap(inputName, inputTensor)
            );

            long[] predictions = (long[]) result.get(0).getValue();
            int prediccionNum = (int) predictions[0];

            String productoRecomendado = encoderProducto.get(String.valueOf(prediccionNum));
            if (productoRecomendado == null) {
                throw new RuntimeException("Producto no encontrado para índice: " + prediccionNum);
            }

            log.info("Wizard predict → [{}, {}, {}] = {} (idx:{})",
                    request.getCategoria(), request.getRangoEdad(),
                    request.getUsoPrevisto(), productoRecomendado, prediccionNum);

            return buildResponse(productoRecomendado);

        } catch (OrtException e) {
            log.error("Error en inferencia ONNX: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la recomendación con el modelo ML", e);
        }
    }

    public com.sigepid.catalog.application.dto.PersonalizedWizardResponse predictPersonalized(Long userId, String usoPrevisto) {
        com.sigepid.catalog.infrastructure.client.UserPreferencesResponse prefs = authServiceClient.getUserPreferences(userId);

        List<WizardResponse> recommendations = new java.util.ArrayList<>();
        if (prefs.getPreferredCategories() != null) {
            for (String category : prefs.getPreferredCategories()) {
                WizardRequest req = WizardRequest.builder()
                        .categoria(category)
                        .rangoEdad(prefs.getAgeRange())
                        .usoPrevisto(usoPrevisto)
                        .build();
                try {
                    recommendations.add(predict(req));
                } catch (Exception e) {
                    log.warn("No se pudo predecir para la categoria {}: {}", category, e.getMessage());
                }
            }
        }

        return com.sigepid.catalog.application.dto.PersonalizedWizardResponse.builder()
                .ageRange(prefs.getAgeRange())
                .preferredCategories(prefs.getPreferredCategories())
                .recommendations(recommendations)
                .build();
    }

    public WizardResponse.WizardOptions getOptions() {
        return wizardOptions;
    }

    private int encodeFeature(Map<String, Integer> encoder, String value, String featureName) {
        Integer encoded = encoder.get(value);
        if (encoded == null) {
            throw new IllegalArgumentException(
                    "Valor inválido para " + featureName + ": '" + value +
                    "'. Valores válidos: " + encoder.keySet()
            );
        }
        return encoded;
    }

    @SuppressWarnings("unchecked")
    private WizardResponse buildResponse(String productoRecomendado) {
        Map<String, Object> stats = productStats.getOrDefault(
                productoRecomendado, java.util.Collections.emptyMap()
        );

        BigDecimal precioPromedio = stats.containsKey("precioPromedio")
                ? BigDecimal.valueOf(((Number) stats.get("precioPromedio")).doubleValue())
                : BigDecimal.ZERO;
        BigDecimal precioMin = stats.containsKey("precioMin")
                ? BigDecimal.valueOf(((Number) stats.get("precioMin")).doubleValue())
                : BigDecimal.ZERO;
        BigDecimal precioMax = stats.containsKey("precioMax")
                ? BigDecimal.valueOf(((Number) stats.get("precioMax")).doubleValue())
                : BigDecimal.ZERO;
        int stockPromedio = stats.containsKey("stockPromedio")
                ? ((Number) stats.get("stockPromedio")).intValue()
                : 100;
        String categoria = stats.containsKey("categoriaPredominante")
                ? (String) stats.get("categoriaPredominante")
                : "";
        String descripcion = stats.containsKey("descripcion")
                ? (String) stats.get("descripcion")
                : "Producto recomendado por el motor ML de SiGePID.";

        return WizardResponse.builder()
                .productoRecomendado(productoRecomendado)
                .descripcion(descripcion)
                .precioPromedio(precioPromedio)
                .precioMin(precioMin)
                .precioMax(precioMax)
                .stockPromedio(stockPromedio)
                .categoriaPredominante(categoria)
                .confianza(1.0)
                .build();
    }

    private Map<String, Integer> castToStringIntMap(Map<String, Object> raw) {
        Map<String, Integer> result = new java.util.HashMap<>();
        raw.forEach((k, v) -> result.put(k, ((Number) v).intValue()));
        return result;
    }
}
