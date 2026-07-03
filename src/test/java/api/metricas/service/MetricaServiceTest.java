package api.metricas.service;

import api.metricas.model.MetricaRentabilidad;
import api.metricas.repository.MetricaRentabilidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricaServiceTest {

    @Mock
    private MetricaRentabilidadRepository metricaRepository;

    @InjectMocks
    private MetricaService metricaService;

    private String sku;
    private Double precioVenta;
    private Double costoOperativo;

    @BeforeEach
    void setUp() {
        sku = "TEST-SKU-123";
        precioVenta = 200.0;
        costoOperativo = 150.0;
    }

    @Test
    @DisplayName("Debería generar y guardar una nueva métrica de rentabilidad")
    void generarMetrica_deberiaCalcularYGuardarMetrica() {
        // Given: Configuramos el comportamiento del mock del repositorio.
        when(metricaRepository.save(any(MetricaRentabilidad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Llamamos al método que queremos probar.
        MetricaRentabilidad resultado = metricaService.generarMetrica(sku, precioVenta, costoOperativo);

        // Then: Verificamos que el resultado no sea nulo y que los cálculos sean correctos.
        assertNotNull(resultado);
        assertEquals(sku, resultado.getSku());
        // Verificamos los campos correctos: margenGanancia y roi
        assertEquals(25.0, resultado.getMargenGanancia(), 0.001); // ((200 - 150) / 200) * 100
        assertEquals(33.33, resultado.getRoi(), 0.001); // ((200 - 150) / 150) * 100

        // Verificamos que el método 'save' del repositorio fue llamado exactamente una vez.
        verify(metricaRepository, times(1)).save(any(MetricaRentabilidad.class));
    }

    @Test
    @DisplayName("Debería devolver una lista de métricas para un SKU dado")
    void obtenerHistorialMetricas_deberiaDevolverListaDelRepositorio() {
        // Given: Creamos una lista falsa de métricas que el repositorio debería devolver.
        MetricaRentabilidad metrica = MetricaRentabilidad.calcularPara(sku, precioVenta, costoOperativo);
        List<MetricaRentabilidad> listaEsperada = Collections.singletonList(metrica);

        // Configuramos el mock para que devuelva nuestra lista falsa cuando se le pida.
        when(metricaRepository.findBySkuOrderByFechaCalculoDesc(sku)).thenReturn(listaEsperada);

        // When: Llamamos al método que queremos probar.
        List<MetricaRentabilidad> resultado = metricaService.obtenerHistorialMetricas(sku);

        // Then: Verificamos que el resultado sea el esperado.
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(listaEsperada, resultado);

        // Verificamos que el método del repositorio fue llamado exactamente una vez con el SKU correcto.
        verify(metricaRepository, times(1)).findBySkuOrderByFechaCalculoDesc(sku);
    }

    @Test
    @DisplayName("Debería devolver una lista vacía si no hay métricas para un SKU")
    void obtenerHistorialMetricas_deberiaDevolverListaVacia() {
        // Given: Configuramos el mock para que devuelva una lista vacía.
        when(metricaRepository.findBySkuOrderByFechaCalculoDesc(sku)).thenReturn(Collections.emptyList());

        // When: Llamamos al método.
        List<MetricaRentabilidad> resultado = metricaService.obtenerHistorialMetricas(sku);

        // Then: Verificamos que el resultado sea una lista vacía.
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        // Verificamos que el método del repositorio fue llamado.
        verify(metricaRepository, times(1)).findBySkuOrderByFechaCalculoDesc(sku);
    }
}
