package lpj.web.developers.auth.com.config;

import java.util.Arrays;
import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración CORS para permitir solicitudes desde cualquier origen.
 * Implementa la interfaz Filter y tiene una prioridad de orden alta.
 * 
 * @author JHLOPEZP
 * @version 1.0
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppCorsConfiguration {

	protected static final Logger LOGGER = Logger.getLogger(AppCorsConfiguration.class.getName());

	// Bean para configurar la fuente de configuración CORS
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		// Creamos una nueva configuración CORS
		CorsConfiguration configuration = new CorsConfiguration();

		// Permitimos solicitudes desde este origen
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

		// Permitimos estos métodos HTTP
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// Permitimos estas cabeceras en las solicitudes
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		// Permitimos el envío de credenciales (cookies, headers de autorización, etc.)
		configuration.setAllowCredentials(true);

		// Configuramos una fuente de configuración basada en URL
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Asociamos la configuración CORS con todos los endpoints de nuestra aplicación
		source.registerCorsConfiguration("/**", configuration);
		// Retornamos la fuente de configuración CORS configurada
		return source;
	}
}
