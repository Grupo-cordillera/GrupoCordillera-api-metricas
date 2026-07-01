package api.metricas.service;

import api.metricas.model.MetricaRentabilidad;
import api.metricas.repository.MetricaRentabilidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // ¡Usamos Lombok para inyectar las dependencias!
public class MetricaService {

    private final MetricaRentabilidadRepository metricaRepository;

    /**
     * Calcula y guarda una nueva métrica de rentabilidad para un producto.
     *
     * @param skuProducto El SKU del producto.
     * @param precioVenta El precio al que se vende el producto.
     * @param costoOperativo El costo asociado a la operación del producto.
     * @return La nueva métrica de rentabilidad guardada.
     */
    @Transactional
    public MetricaRentabilidad generarMetrica(String skuProducto, Double precioVenta, Double costoOperativo) {
        // Ya no necesitamos buscar el producto. Confiamos en el SKU.
        // Delegamos toda la lógica de cálculo al método estático de la entidad.
        MetricaRentabilidad nuevaMetrica = MetricaRentabilidad.calcularPara(skuProducto, precioVenta, costoOperativo);

        // Guardamos la nueva métrica en la base de datos de este microservicio.
        return metricaRepository.save(nuevaMetrica);
    }

    /**
     * Obtiene el historial de métricas para un producto específico.
     *
     * @param sku El SKU del producto.
     * @return Una lista de métricas ordenadas por fecha descendente.
     */
    public List<MetricaRentabilidad> obtenerHistorialMetricas(String sku) {
        // Usamos el método que definimos en el repositorio.
        return metricaRepository.findBySkuOrderByFechaCalculoDesc(sku);
    }
}