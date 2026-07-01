package api.metricas.repository;

import api.metricas.model.MetricaRentabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricaRentabilidadRepository extends JpaRepository<MetricaRentabilidad, Long> {

    /**
     * Busca y devuelve el  historial de métricas para un producto específico,
     * ordenado por la fecha de cálculo más reciente primero.
     *
     * @param sku El identificador único del producto.
     * @return Una lista de métricas.
     */
    List<MetricaRentabilidad> findBySkuOrderByFechaCalculoDesc(String sku);
}