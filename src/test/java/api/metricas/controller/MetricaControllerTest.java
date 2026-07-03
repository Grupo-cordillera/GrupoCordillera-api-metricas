package api.metricas.controller;

import api.metricas.model.MetricaRentabilidad;
import api.metricas.dto.MetricaRequest;
import api.metricas.dto.MetricaResponse;
import api.metricas.service.MetricaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MetricaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MetricaService metricaService;

    @InjectMocks
    private MetricaController metricaController;

    @BeforeEach
    void setUp() {
        // Usamos el enfoque "standalone" que no requiere el contexto de Spring
        mockMvc = MockMvcBuilders.standaloneSetup(metricaController).build();
    }

    @Test
    @DisplayName("POST /api/metricas debería crear una métrica y devolver 201 Created")
    void calcularMetrica_deberiaCrearMetricaYDevolver201() throws Exception {
        // Given
        String sku = "TEST-SKU-123";
        double precioVenta = 200.0;
        double costoOperativo = 150.0;

        MetricaRequest request = new MetricaRequest(sku, precioVenta, costoOperativo);
        MetricaRentabilidad metricaCalculada = MetricaRentabilidad.calcularPara(sku, precioVenta, costoOperativo);

        // Configuramos el mock del servicio
        when(metricaService.generarMetrica(anyString(), anyDouble(), anyDouble())).thenReturn(metricaCalculada);

        // When & Then
        mockMvc.perform(post("/api/metricas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sku").value(sku))
                .andExpect(jsonPath("$.margenGanancia").value(25.0))
                .andExpect(jsonPath("$.roi").value(33.33));
    }

    @Test
    @DisplayName("GET /api/metricas/{sku} debería devolver el historial de métricas y 200 OK")
    void obtenerHistorialMetricas_deberiaDevolverHistorialY200() throws Exception {
        // Given
        String sku = "TEST-SKU-123";
        MetricaRentabilidad metrica = MetricaRentabilidad.calcularPara(sku, 200.0, 150.0);
        
        // Configuramos el mock del servicio
        when(metricaService.obtenerHistorialMetricas(sku)).thenReturn(Collections.singletonList(metrica));

        // When & Then
        mockMvc.perform(get("/api/metricas/{sku}", sku))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].sku").value(sku))
                .andExpect(jsonPath("$[0].margenGanancia").value(25.0));
    }
}
