package lpj.web.developers.auth.com.service;

import lpj.web.developers.auth.com.controller.RestExceptionHandler;
import lpj.web.developers.auth.com.domain.InformationAditionalUser;
import lpj.web.developers.auth.com.domain.User;
import lpj.web.developers.auth.com.exceptions.NotFoundException;
import lpj.web.developers.auth.com.exceptions.UserBlockedException;
import lpj.web.developers.auth.com.exceptions.UserNotActiveException;
import lpj.web.developers.auth.com.models.mappers.UserRowMapper;
import lpj.web.developers.auth.com.repository.LoginAttemptRepository;
import lpj.web.developers.auth.com.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	private final UserRepository repository;
	private final LoginAttemptRepository loginAttemptRepository;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// Se inyecta UserRepository como dependencia a través del constructor
	public UserDetailsServiceImpl(UserRepository repository, LoginAttemptRepository loginAttemptRepository,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.repository = repository;
		this.loginAttemptRepository = loginAttemptRepository;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	// Método de carga de usuario por nombre de usuario para la autenticación
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) {
	    try {
	        // Intenta buscar el usuario en el repositorio usando su dirección de correo electrónico
	        User user = repository.findByEmail(email)
	                .orElseThrow(() -> new NotFoundException(String.format("El usuario no existe, correo electrónico: %s", email)));

	        // Crea roles/autoridades otorgadas para el usuario
	        List<GrantedAuthority> roles = new ArrayList<>();
	        roles.add(new SimpleGrantedAuthority(user.perfil()));

	        // Registra en el log que el inicio de sesión fue exitoso para el usuario
	        LOG.info("Inicio de sesión exitoso para el usuario: {}", email);

	        // Construye y devuelve los detalles del usuario
	        return new org.springframework.security.core.userdetails.User(
	                user.email(),       // Nombre de usuario (correo electrónico)
	                user.password(),    // Contraseña
	                roles               // Lista de roles/autoridades otorgadas
	        );
	    } catch (UserNotActiveException | UsernameNotFoundException | BadCredentialsException | NotFoundException | UserBlockedException ex) {
	        // Registra en el log que hubo un intento de inicio de sesión fallido para el usuario
	        LOG.warn("Intento de inicio de sesión fallido para el usuario: {}", email);

	        // Relanza la excepción para manejarla en capas superiores
	        throw ex;
	    }
	}

}
