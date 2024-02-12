package lpj.web.developers.auth.com.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lpj.web.developers.auth.com.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserDetailsServiceImpl userDetailsService;
  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtAuthFilter = jwtAuthFilter;
  }

  // Bean para proporcionar un codificador de contraseñas
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Configuración principal de seguridad y cadena de filtros
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    return http
//        .cors(AbstractHttpConfigurer::disable)  // Deshabilitar la configuración de CORS
        .csrf(AbstractHttpConfigurer::disable)  // Deshabilitar la protección CSRF
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Establecer la política de creación de sesiones como SIN SESIÓN

        // Establecer permisos en los endpoints
        .authorizeHttpRequests(auth -> auth
            // Nuestros endpoints públicos
            .requestMatchers(HttpMethod.POST, "/api/auth/signup/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/authentication-docs/**").permitAll()
            // Nuestros endpoints privados
            .anyRequest().authenticated())
        
        .authenticationManager(authenticationManager)

        // Necesitamos el filtro JWT antes del UsernamePasswordAuthenticationFilter.
        // Dado que necesitamos autenticar cada solicitud antes de pasar por el filtro de seguridad de Spring.
        // (UsernamePasswordAuthenticationFilter crea un UsernamePasswordAuthenticationToken a partir de un nombre de usuario y contraseña que se envían en HttpServletRequest.)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  // Bean para proporcionar un AuthenticationManager personalizado
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    return authenticationManagerBuilder.build();
  }
  
  
 
  
}

