package api.metricas.controller;

import api.metricas.dto.MetricaRequest;
import api.metricas.dto.MetricaResponse;
import api.metricas.model.MetricaRentabilidad;
import api.metricas.service.MetricaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metricas") // La URL base para todos los endpoints de este controlador
@RequiredArgsConstructor
public class MetricaController {

    private final MetricaService metricaService;

    /**
     * Endpoint para calcular y registrar una nueva métrica de rentabilidad.
     * Recibe los datos en el cuerpo de la petición.
     */
    @PostMapping
    public ResponseEntity<MetricaResponse> calcularMetrica(@Valid @RequestBody MetricaRequest request) {
        MetricaRentabilidad nuevaMetrica = metricaService.generarMetrica(
                request.sku(),
                request.precioVenta(),
                request.costoOperativo()
        );
        // Convertimos la entidad a nuestro DTO de respuesta y devolvemos un código 201 (Created)
        return new ResponseEntity<>(MetricaResponse.fromEntity(nuevaMetrica), HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener el historial de métricas de un producto específico.
     * El SKU del producto se pasa como parte de la URL.
     */
    @GetMapping("/{sku}")
    public ResponseEntity<List<MetricaResponse>> obtenerHistorialMetricas(@PathVariable String sku) {
        List<MetricaRentabilidad> historial = metricaService.obtenerHistorialMetricas(sku);

        // Convertimos la lista de entidades a una lista de DTOs de respuesta
        List<MetricaResponse> respuesta = historial.stream()
                .map(MetricaResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }
}