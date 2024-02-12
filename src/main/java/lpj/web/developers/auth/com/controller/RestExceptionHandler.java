package lpj.web.developers.auth.com.controller;

import lpj.web.developers.auth.com.controller.dto.ApiErrorResponse;
import lpj.web.developers.auth.com.exceptions.DuplicateException;
import lpj.web.developers.auth.com.exceptions.NotFoundException;
import lpj.web.developers.auth.com.exceptions.UserNotActiveException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {


	  @ExceptionHandler(NotFoundException.class)
	    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
	        ApiErrorResponse errorResponse = new ApiErrorResponse(
	                HttpStatus.NOT_FOUND.value(),
	                ex.getMessage(),
	                "Not Found",
	                "USER-NOT-FOUND-001"
	        );
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	    }
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.add(error.getField() + ": " + error.getDefaultMessage()));
        e.getBindingResult().getGlobalErrors().forEach(error -> errors.add(error.getObjectName() + ": " + error.getDefaultMessage()));

        String message = "La validación de la solicitud falló: %s".formatted(String.join(", ", errors));
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message, "Bad Request", "BAD-REQUEST-001");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotActiveException(UserNotActiveException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Usuario bloqueado, Por favor, pongase en contacto con su administrador", 
            "UserNotActiveError", // Puedes ajustar este valor según tus necesidades
            "USER-NOT-ACTIVE-001" // Puedes ajustar este valor según tus necesidades
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Usuario o contraseña inválidos", "Unauthorized", "UNAUTHORIZED-001");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateException(DuplicateException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage(), "Conflict", "CONFLICT-001");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(),
        		"El usuario no esta registrado o no tiene los privilegios necesarios", "Unauthorized", "UNAUTHORIZED-002");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }


     
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleExceptions(Exception e) {
        // Lógica de manejo de excepciones genéricas
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor", "Internal Server Error", "INTERNAL-SERVER-ERROR-001");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}


