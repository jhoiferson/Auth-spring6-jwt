package lpj.web.developers.auth.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lpj.web.developers.auth.com.controller.dto.ApiErrorResponse;
import lpj.web.developers.auth.com.controller.dto.LoginAttemptResponse;
import lpj.web.developers.auth.com.controller.dto.LoginRequest;
import lpj.web.developers.auth.com.controller.dto.LoginResponse;
import lpj.web.developers.auth.com.controller.dto.SignupRequest;
import lpj.web.developers.auth.com.domain.LoginAttempt;
import lpj.web.developers.auth.com.exceptions.DuplicateException;
import lpj.web.developers.auth.com.exceptions.NotFoundException;
import lpj.web.developers.auth.com.exceptions.UserNotActiveException;
import lpj.web.developers.auth.com.helper.JwtHelper;
import lpj.web.developers.auth.com.repository.UserRepository;
import lpj.web.developers.auth.com.service.LoginService;
import lpj.web.developers.auth.com.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final LoginService loginService;
    private final UserRepository userrep;

    // Constructor que recibe instancias necesarias para la autenticación y
    // servicios de usuario
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
            LoginService loginService, UserRepository userrep) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.loginService = loginService;
        this.userrep = userrep;
    }

    // Endpoint para registrar un nuevo usuario
    @Operation(summary = "Registrar usuario")
    @ApiResponse(responseCode = "201")
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest requestDto) {
        try {
            userService.signup(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente");
        } catch (DuplicateException e) {
            throw e; // Permitir que la excepción se propague y sea manejada por RestExceptionHandler
        }
    }

    // Endpoint para autenticar un usuario y obtener un token
    @Operation(summary = "Autenticar usuario y devolver token")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            // Si la autenticación es exitosa, restablecer los intentos fallidos
            // y obtener información adicional del usuario autenticado
            loginService.resetFailedAttempts(request.email());

            // Obtener datos adicionales del usuario
            Map<String, Object> additionalUserData = loginService.getAdditionalUserData(request.email());
           

            // Obtener el valor del campo "estado" de additionalUserData
            Object estadoObject = additionalUserData.get("estado");
            int status = Integer.parseInt((String) estadoObject);
  
            if (status == 0) {
                // El usuario está bloqueado, lanzar excepción UserNotActiveException
                throw new UserNotActiveException();
            }

            // Generar el token con información adicional
            String token = JwtHelper.generateToken(request.email(), additionalUserData);

            // Construir el JSON de respuesta con el token y la información adicional
            response.put("email", request.email());
            response.put("token", token);
            response.put("additionalInfo", additionalUserData);

            return ResponseEntity.ok(response);
        } catch (UserNotActiveException ex) {
            // Registra en el log que hubo un intento de inicio de sesión fallido para el
            // usuario
            LOG.warn("Intento de inicio de sesión fallido para el usuario: {}", request.email());

            throw ex;
        } catch (NotFoundException ex) {
            // Manejar la excepción específica cuando el usuario no existe
//            LOG.warn("Intento de inicio de sesión fallido para el usuario: {}", request.email());
//            ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), "Usuario no encontrado",
//                    "Not Found", "USER-NOT-FOUND-001");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        	throw ex;
        } catch (BadCredentialsException e) {
            // Verificar si el usuario está bloqueado.
            if (loginService.isUserBlocked(request.email())) {
            	throw new UserNotActiveException();
            }
            // Verificar si el usuario ya tiene un intento registrado
            if (!loginService.isUserRegistered(request.email())) {
                // Si no tiene un intento registrado, incrementar los intentos fallidos y
                // guardarlo
                loginService.addLoginAttempt(request.email(), false);
            } else {
                // Si ya tiene un intento registrado, simplemente actualizar el contador de
                // intentos fallidos
                loginService.incrementFailedAttempts(request.email());
            }

            // Obtener la cantidad de intentos fallidos del usuario
            int failedAttempts = loginService.getFailedAttempts(request.email());

            try {
                if (failedAttempts >= 3) {
                    loginService.getUpdateStatusUser(request.email());

                    throw new UserNotActiveException();
                } 
                else {
                    // Modificar el mensaje según la cantidad de intentos restantes
                    int remainingAttempts = 3 - failedAttempts;

                    String errorMessage = (remainingAttempts == 1)
                            ? "Contraseña incorrecta. Solo le queda 1 intento antes de que su usuario sea bloqueado."
                            : String.format("Contraseña incorrecta. Solo le quedan %d intentos antes de que su usuario sea bloqueado.",
                                    remainingAttempts);

                    response.put("mensaje", errorMessage);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            catch (NotFoundException ex) {
                // Manejar la excepción específica cuando el usuario no existe
            	System.out.println("entor a esta excepcion");
                response.put("mensaje", "El usuario no existe.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        }
    }

    // Endpoint para obtener intentos de inicio de sesión recientes
    @Operation(summary = "Obtener intentos de inicio de sesión recientes")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping(value = "/loginAttempts")
    public ResponseEntity<List<LoginAttemptResponse>> loginAttempts(@RequestHeader("Authorization") String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        List<LoginAttempt> loginAttempts = loginService.findRecentLoginAttempts(email);
        return ResponseEntity.ok(convertToDTOs(loginAttempts));
    }

    // Método auxiliar para convertir entidades de intentos de inicio de sesión a
    // DTOs
    private List<LoginAttemptResponse> convertToDTOs(List<LoginAttempt> loginAttempts) {
        return loginAttempts.stream().map(LoginAttemptResponse::convertToDTO).collect(Collectors.toList());
    }

}
