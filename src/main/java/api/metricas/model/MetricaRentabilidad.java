package api.metricas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "metrica_rentabilidad")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetricaRentabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    // En lugar de la relación, guardamos el SKU del producto como un simple String.
    // Este es el "enlace" entre el microservicio de métricas y el de inventario.
    @Column(name = "sku", nullable = false)
    @Setter(AccessLevel.NONE)
    private String sku;

    @Column(name = "margen_ganancia")
    private Double margenGanancia;

    @Column(name = "costo_operativo")
    private Double costoOperativo;

    @Column(name = "roi")
    private Double roi;

    @Column(name = "fecha_calculo")
    private LocalDate fechaCalculo;

    // 1. Constructor privado adaptado para recibir el SKU
    private MetricaRentabilidad(String sku, Double costoOperativo, Double margenGanancia, Double roi) {
        this.sku = sku;
        this.costoOperativo = costoOperativo;
        this.margenGanancia = margenGanancia;
        this.roi = roi;
        this.fechaCalculo = LocalDate.now();
    }

    // 2. Factory Method adaptado para recibir el SKU
    public static MetricaRentabilidad calcularPara(String sku, Double precioVenta, Double costoOperativo) {

        if (costoOperativo == null || costoOperativo <= 0) {
            throw new IllegalArgumentException("El costo operativo debe ser mayor a cero para calcular métricas");
        }
        if (precioVenta == null || precioVenta <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a cero");
        }

        Double beneficioNeto = precioVenta - costoOperativo;
        Double margenCalculado = (beneficioNeto / precioVenta) * 100.0;
        Double roiCalculado = (beneficioNeto / costoOperativo) * 100.0;

        margenCalculado = Math.round(margenCalculado * 100.0) / 100.0;
        roiCalculado = Math.round(roiCalculado * 100.0) / 100.0;

        return new MetricaRentabilidad(sku, costoOperativo, margenCalculado, roiCalculado);
    }
}