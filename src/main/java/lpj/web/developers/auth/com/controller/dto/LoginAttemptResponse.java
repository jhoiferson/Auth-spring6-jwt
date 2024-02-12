package lpj.web.developers.auth.com.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lpj.web.developers.auth.com.domain.LoginAttempt;

import java.time.LocalDateTime;

// La clase representa un DTO para la respuesta de intentos de inicio de sesión
public record LoginAttemptResponse(
    // La anotación @Schema se utiliza para proporcionar información adicional para la documentación OpenAPI
    @Schema(description = "La fecha y hora del intento de inicio de sesión") LocalDateTime createdAt,
    @Schema(description = "El estado de inicio de sesión") boolean success) {

  // Método estático para convertir una entidad LoginAttempt a un objeto LoginAttemptResponse
  public static LoginAttemptResponse convertToDTO(LoginAttempt loginAttempt) {
    // Crea una nueva instancia de LoginAttemptResponse utilizando la información del intento de inicio de sesión
    return new LoginAttemptResponse(loginAttempt.createdAt(), loginAttempt.success());
  }
}
