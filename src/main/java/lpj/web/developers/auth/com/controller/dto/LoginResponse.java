package lpj.web.developers.auth.com.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// La clase representa un DTO para la respuesta de inicio de sesión
public record LoginResponse(
    // La anotación @Schema se utiliza para proporcionar información adicional para la documentación OpenAPI
    @Schema(description = "Correo electrónico")
    String email,
    
    @Schema(description = "Token JWT")
    String token) {

}
