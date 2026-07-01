package api.metricas.dto;

import api.metricas.model.MetricaRentabilidad;

import java.time.LocalDate;

// También usamos un 'record' para la respuesta.
public record MetricaResponse(
        Long id,
        String sku,
        Double margenGanancia,
        Double costoOperativo,
        Double roi,
        LocalDate fechaCalculo
) {
    /**
     * Método de fábrica para convertir una entidad MetricaRentabilidad
     * en un DTO de respuesta.
     */
    public static MetricaResponse fromEntity(MetricaRentabilidad metrica) {
        return new MetricaResponse(
                metrica.getId(),
                metrica.getSku(),
                metrica.getMargenGanancia(),
                metrica.getCostoOperativo(),
                metrica.getRoi(),
                metrica.getFechaCalculo()
        );
    }
}