package lpj.web.developers.auth.com.service;

import lpj.web.developers.auth.com.controller.dto.LoginRequest;
import lpj.web.developers.auth.com.domain.InformationAditionalUser;
import lpj.web.developers.auth.com.domain.LoginAttempt;
import lpj.web.developers.auth.com.domain.User;
import lpj.web.developers.auth.com.exceptions.UserBlockedException;
import lpj.web.developers.auth.com.exceptions.UserNotActiveException;
import lpj.web.developers.auth.com.models.mappers.UserRowMapper;
import lpj.web.developers.auth.com.repository.LoginAttemptRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginService {

	private final LoginAttemptRepository loginAttemptRepository;
	private final AuthenticationManager authenticationManager;

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

   
	
	public LoginService(LoginAttemptRepository loginAttemptRepository, 
			AuthenticationManager authenticationManager,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.loginAttemptRepository = loginAttemptRepository;
		this.authenticationManager = authenticationManager;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Transactional
	public void addLoginAttempt(String email, boolean success) {
		LoginAttempt loginAttempt = new LoginAttempt(email, success, LocalDateTime.now(), 0);
		loginAttemptRepository.add(loginAttempt);
	}

	public List<LoginAttempt> findRecentLoginAttempts(String email) {
		return loginAttemptRepository.findRecent(email);
	}


	@Transactional
	public void resetFailedAttempts(String email) {
		loginAttemptRepository.resetFailedAttempts(email);
	}

	public boolean isUserBlocked(String email) {
		return loginAttemptRepository.isUserBlocked(email);
	}

	
	 public boolean isUserRegistered(String email) {
	        // Implementa la lógica para verificar si el usuario ya tiene un intento registrado
	        // Puedes usar el método correspondiente de tu repositorio o cualquier otra lógica necesaria
	        return loginAttemptRepository.isUserRegistered(email);
	    }
	 
	 @Transactional
	    public void incrementFailedAttempts(String email) {
	        // Implementa la lógica para incrementar los intentos fallidos
	        // Puedes usar el método correspondiente de tu repositorio o cualquier otra lógica necesaria
	        loginAttemptRepository.incrementFailedAttempts(email);
	    }
	 
	 public int getFailedAttempts(String email) {
		    return loginAttemptRepository.getFailedAttempts(email);
		}
	 
	 public Map<String, Object> getAdditionalUserData(String email) {
		    return loginAttemptRepository.getAdditionalUserData(email);
		}
	 
	 @Transactional
	 public void getUpdateStatusUser(String email) {
		     loginAttemptRepository.updateStatusUser(email);
		}

}
