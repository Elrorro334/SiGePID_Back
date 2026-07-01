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

/**
 * Servicio de inferencia del árbol de decisión usando ONNX Runtime.
 * El modelo clasifica una combinación (categoría, rangoEdad, usoPrevisto)
 * y retorna el producto recomendado junto con sus estadísticas del dataset.
 */
@Service
@Slf4j
public class WizardService {

    private OrtEnvironment ortEnv;
    private OrtSession ortSession;

    // Mapeos de codificación cargados desde encoder_mappings.json
    private Map<String, Integer> encoderCategoria;
    private Map<String, Integer> encoderRangoEdad;
    private Map<String, Integer> encoderUsoPrevisto;
    private Map<String, String>  encoderProducto; // índice (como string) → nombre

    // Estadísticas del dataset por producto
    private Map<String, Map<String, Object>> productStats;

    // Opciones disponibles para el wizard
    private WizardResponse.WizardOptions wizardOptions;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================================================================
    // INICIALIZACIÓN: carga el modelo ONNX y los mapeos al arrancar el servicio
    // =========================================================================
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        try {
            log.info("Iniciando WizardService: cargando modelo ONNX y encoders...");

            // 1. Cargar modelo ONNX desde resources/ml/
            ortEnv = OrtEnvironment.getEnvironment();
            ClassPathResource modelResource = new ClassPathResource("ml/modelo_arbol_sigepid.onnx");
            byte[] modelBytes = modelResource.getInputStream().readAllBytes();
            ortSession = ortEnv.createSession(modelBytes, new OrtSession.SessionOptions());
            log.info("Modelo ONNX cargado. Inputs: {}, Outputs: {}",
                    ortSession.getInputNames(), ortSession.getOutputNames());

            // 2. Cargar mapeos de encoders desde resources/ml/encoder_mappings.json
            ClassPathResource mappingsResource = new ClassPathResource("ml/encoder_mappings.json");
            Map<String, Object> mappingsFile = objectMapper.readValue(
                    mappingsResource.getInputStream(),
                    new TypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> encoders = (Map<String, Object>) mappingsFile.get("encoders");
            encoderCategoria   = castToStringIntMap((Map<String, Object>) encoders.get("categoria"));
            encoderRangoEdad   = castToStringIntMap((Map<String, Object>) encoders.get("rango_edad"));
            encoderUsoPrevisto = castToStringIntMap((Map<String, Object>) encoders.get("uso_previsto"));

            // Para producto: las claves vienen como "0", "1", etc. (JSON object keys son strings)
            Map<String, Object> rawProducto = (Map<String, Object>) encoders.get("producto");
            encoderProducto = new java.util.HashMap<>();
            rawProducto.forEach((k, v) -> encoderProducto.put(k, (String) v));

            // 3. Cargar opciones del wizard
            Map<String, Object> options = (Map<String, Object>) mappingsFile.get("options");
            wizardOptions = WizardResponse.WizardOptions.builder()
                    .categorias((List<String>) options.get("categorias"))
                    .rangosEdad((List<String>) options.get("rangosEdad"))
                    .usosPrevisto((List<String>) options.get("usosPrevisto"))
                    .build();

            // 4. Cargar estadísticas del dataset
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

    // =========================================================================
    // PREDICCIÓN
    // =========================================================================

    /**
     * Realiza la inferencia con el árbol de decisión y retorna el producto
     * recomendado junto con sus estadísticas del dataset.
     */
    public WizardResponse predict(WizardRequest request) {
        try {
            // 1. Codificar los inputs a valores numéricos
            int catNum  = encodeFeature(encoderCategoria, request.getCategoria(), "categoria");
            int edadNum = encodeFeature(encoderRangoEdad, request.getRangoEdad(), "rangoEdad");
            int usoNum  = encodeFeature(encoderUsoPrevisto, request.getUsoPrevisto(), "usoPrevisto");

            log.debug("Inputs codificados → categoria:{}, rangoEdad:{}, usoPrevisto:{}",
                    catNum, edadNum, usoNum);

            // 2. Crear tensor de entrada: shape [1, 3] con dtype int64
            long[][] inputData = {{catNum, edadNum, usoNum}};
            OnnxTensor inputTensor = OnnxTensor.createTensor(ortEnv, inputData);

            // 3. Ejecutar inferencia
            String inputName = ortSession.getInputNames().iterator().next();
            OrtSession.Result result = ortSession.run(
                    java.util.Collections.singletonMap(inputName, inputTensor)
            );

            // 4. Extraer la predicción (primera salida = etiqueta de clase)
            long[] predictions = (long[]) result.get(0).getValue();
            int prediccionNum = (int) predictions[0];

            // 5. Decodificar el número al nombre del producto
            String productoRecomendado = encoderProducto.get(String.valueOf(prediccionNum));
            if (productoRecomendado == null) {
                throw new RuntimeException("Producto no encontrado para índice: " + prediccionNum);
            }

            log.info("Wizard predict → [{}, {}, {}] = {} (idx:{})",
                    request.getCategoria(), request.getRangoEdad(),
                    request.getUsoPrevisto(), productoRecomendado, prediccionNum);

            // 6. Obtener estadísticas del dataset para el producto
            return buildResponse(productoRecomendado);

        } catch (OrtException e) {
            log.error("Error en inferencia ONNX: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la recomendación con el modelo ML", e);
        }
    }

    /**
     * Retorna las opciones disponibles del wizard para que el frontend
     * pueda construir las preguntas dinámicamente.
     */
    public WizardResponse.WizardOptions getOptions() {
        return wizardOptions;
    }

    // =========================================================================
    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================================

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
                .confianza(1.0) // El árbol de decisión determinista tiene confianza máxima
                .build();
    }

    private Map<String, Integer> castToStringIntMap(Map<String, Object> raw) {
        Map<String, Integer> result = new java.util.HashMap<>();
        raw.forEach((k, v) -> result.put(k, ((Number) v).intValue()));
        return result;
    }
}
