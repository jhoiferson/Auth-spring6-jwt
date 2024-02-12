package lpj.web.developers.auth.com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lpj.web.developers.auth.com.controller.dto.ApiErrorResponse;
import lpj.web.developers.auth.com.exceptions.AccessDeniedException;
import lpj.web.developers.auth.com.helper.JwtHelper;
import lpj.web.developers.auth.com.service.UserDetailsServiceImpl;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * @author LPJWEBDEVELOPERS
 * @developer JHLOPEZP
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl userDetailsService;
  private final ObjectMapper objectMapper;

  // Constructor que recibe instancias de UserDetailsServiceImpl y ObjectMapper
  public JwtAuthFilter(UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper) {
    this.userDetailsService = userDetailsService;
    this.objectMapper = objectMapper;
  }

  // Método para filtrar las solicitudes
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Obtener el encabezado de autorización de la solicitud
      String authHeader = request.getHeader("Authorization");

      String token = null;
      String username = null;
      // Verificar si el encabezado contiene un token de tipo Bearer
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        // Extraer el nombre de usuario del token
        username = JwtHelper.extractUsername(token);
      }

      // Si no hay token, continuar con la cadena de filtros
      if (token == null) {
        filterChain.doFilter(request, response);
        return;
      }

      // Si hay un nombre de usuario y no hay autenticación en el contexto de seguridad
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // Cargar los detalles del usuario utilizando el servicio UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // Validar el token con los detalles del usuario
        if (JwtHelper.validateToken(token, userDetails)) {
          // Crear un objeto de autenticación y establecerlo en el contexto de seguridad
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }

      // Continuar con la cadena de filtros
      filterChain.doFilter(request, response);
    } 
    catch (AccessDeniedException e) {
        // Capturar excepción de acceso denegado y devolver una respuesta de error
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado", "Forbidden", "FORBIDDEN-001");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(toJson(errorResponse));
    } catch (Exception e) {
        // Capturar otras excepciones y devolver una respuesta de error
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor", "Internal Server Error", "INTERNAL-SERVER-ERROR-001");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(toJson(errorResponse));
    }
  }

//Método para convertir un objeto ApiErrorResponse a formato JSON
private String toJson(ApiErrorResponse response) {
   try {
       return objectMapper.writeValueAsString(response);
   } catch (Exception e) {
       // Devulve un mensaje personalizado en lugar de una cadena vacía
       return "{\"error\": \"Error al convertir la respuesta a JSON\", \"details\": \"" + e.getMessage() + "\"}";
   }
}

}
