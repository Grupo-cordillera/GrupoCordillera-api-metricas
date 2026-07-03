package api.metricas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JwtAuthenticationFilterTest {

    // Usamos una clave secreta consistente para las pruebas
    private static final String SECRET = "956b86d50cee0b1bd440f15afb780ab337c40948b3bea80777810e8365f04128";

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter();
        // Inyectamos el secreto en el filtro usando ReflectionTestUtils, como en el ejemplo.
        ReflectionTestUtils.setField(filter, "jwtSecret", SECRET);
    }

    @AfterEach
    void cleanup() {
        // Limpiamos el contexto de seguridad después de cada prueba.
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debería establecer la autenticación si el token es válido")
    void doFilterInternal_conTokenValido_deberiaEstablecerAutenticacion() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // Creamos un token JWT válido para la prueba
        String token = Jwts.builder()
                .setSubject("testuser@example.com")
                .claim("rol", "ROLE_ADMIN")
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "La autenticación no debería ser nula");
        assertEquals("testuser@example.com", auth.getName());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")), "El usuario debería tener el rol ROLE_ADMIN");
        
        // Verificamos que la cadena de filtros continúa
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("No debería establecer autenticación si no hay token")
    void doFilterInternal_sinToken_noDeberiaAutenticar() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "La autenticación debería ser nula si no hay token");
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("No debería establecer autenticación si el token es inválido")
    void doFilterInternal_conTokenInvalido_noDeberiaAutenticar() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        request.addHeader("Authorization", "Bearer token-invalido");

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "La autenticación debería ser nula si el token es inválido");
        verify(chain).doFilter(request, response);
    }
}
