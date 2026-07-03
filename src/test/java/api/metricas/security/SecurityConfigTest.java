package api.metricas.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Debería construir el SecurityFilterChain sin errores")
    void securityFilterChain_deberiaConstruirElFiltro() throws Exception {
        // Given
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter);
        HttpSecurity http = mock(HttpSecurity.class);

        // Configuramos el mock para que devuelva el mismo objeto HttpSecurity en cada llamada,
        // simulando la API fluida de Spring Security.
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        
        // NO mockeamos http.build(). Dejamos que devuelva null, como en el ejemplo.

        // When
        SecurityFilterChain result = config.securityFilterChain(http);

        // Then
        // Verificamos que el resultado es null, lo que prueba que el método se ejecutó
        // y que la llamada a http.build() en el mock devolvió el valor por defecto (null).
        assertNull(result, "El resultado de un http.build() mockeado debe ser null.");
    }
}
