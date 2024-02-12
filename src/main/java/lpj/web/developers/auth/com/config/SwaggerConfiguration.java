package lpj.web.developers.auth.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfiguration {

  // Bean que configura la documentación OpenAPI
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        // Configurar el esquema de seguridad Bearer Authentication
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        // Configurar los componentes, incluyendo el esquema de seguridad
        .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
        // Configurar la información general de la API
        .info(apiInfo());
  }

  // Método para crear el esquema de seguridad Bearer Authentication
  private SecurityScheme createAPIKeyScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP)
        .bearerFormat("JWT")
        .scheme("bearer");
  }

  // Método para configurar la información general de la API
  private Info apiInfo() {
    return new Info()
        .title("Authentication Service Api Doc") // Título de la documentación
        .version("1.0.0") // Versión de la API
        .description("HTTP APIs para gestionar el registro y la autenticación de usuarios.") // Descripción de la API
        .contact(new Contact().name("LPJ WEB DEVELOPERS - jhlopezp - 947226568")); // Información de contacto del creador de la API
  }
}
