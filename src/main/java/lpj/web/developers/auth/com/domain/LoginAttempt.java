package lpj.web.developers.auth.com.domain;

import java.time.LocalDateTime;

// La clase representa un registro de intento de inicio de sesión
public record LoginAttempt(String email, boolean success, LocalDateTime createdAt, int failedAttempts) {

}
