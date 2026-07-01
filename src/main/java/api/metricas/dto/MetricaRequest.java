package api.metricas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Usamos un 'record' de Java para un DTO inmutable y conciso.
// Lombok no es necesario aquí.
public record MetricaRequest(
        @NotBlank(message = "El SKU no puede estar vacío")
        String sku,

        @NotNull(message = "El precio de venta no puede ser nulo")
        @Positive(message = "El precio de venta debe ser mayor que cero")
        Double precioVenta,

        @NotNull(message = "El costo operativo no puede ser nulo")
        @Positive(message = "El costo operativo debe ser mayor que cero")
        Double costoOperativo
) {
}