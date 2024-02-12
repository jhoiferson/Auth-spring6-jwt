package lpj.web.developers.auth.com.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lpj.web.developers.auth.com.exceptions.AccessDeniedException;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Base64;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;

// Clase de ayuda para manejar tokens JWT
public class JwtHelper {

    // Clave secreta obtenida de la configuración
    @Value("${jwt.secretKey}")
    private String secretKey;

    // Clave utilizada para firmar los tokens
    private Key signingKey;

    // Inicialización de la clase, convierte la clave secreta en una clave para firmar
    @PostConstruct
    private void init() {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    // Tamaño de la clave en bits (ajústalo según tus necesidades)
    private static final int KEY_SIZE_BITS = 256;

    // Genera una nueva clave HMAC
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(new byte[KEY_SIZE_BITS / 8]);

    // Tiempo de duración del token en minutos
    private static final int TOKEN_EXPIRATION_MINUTES = 60;


    // Método para generar un token JWT con el nombre de usuario proporcionado y la información adicional
    public static String generateToken(String email, Map<String, Object> additionalData) {
        var now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .addClaims(additionalData) // Agregar datos adicionales al cuerpo del token
                .signWith(SECRET_KEY)
                .compact();
    }

    

    // Método para extraer el nombre de usuario del token
    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    // Método para validar un token comparándolo con la información del usuario
    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Método privado para obtener los reclamos del token
    private static Claims getTokenBody(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException | io.jsonwebtoken.security.SignatureException e) {
            throw new AccessDeniedException("Acceso denegado: " + e.getMessage());
        }
    }

    // Método privado para verificar si el token ha caducado
    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}
