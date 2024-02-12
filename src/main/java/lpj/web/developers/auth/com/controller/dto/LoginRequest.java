package lpj.web.developers.auth.com.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// La clase representa un DTO para la solicitud de inicio de sesión
public record LoginRequest(
    // La anotación @Schema se utiliza para proporcionar información adicional para la documentación OpenAPI
    @Schema(description = "Correo electrónico", example = "jhlopezp@gmail.com")
    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    @Email(message = "Formato de correo inválido")
    String email,

    @Schema(description = "Contraseña", example = "123456")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres.")
    String password) {

}
