package lpj.web.developers.auth.com.service;

import lpj.web.developers.auth.com.controller.dto.SignupRequest;
import lpj.web.developers.auth.com.domain.User;
import lpj.web.developers.auth.com.exceptions.DuplicateException;
import lpj.web.developers.auth.com.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void signup(SignupRequest request) {
    String email = request.email();
    Optional<User> existingUser = repository.findByEmail(email);
    if (existingUser.isPresent()) {
    	throw new DuplicateException(String.format("El usuario con la dirección "
    			+ "de correo electrónico '%s' ya existe.", email));
    }

    String hashedPassword = passwordEncoder.encode(request.password());
    User user = new User(request.name(), email, hashedPassword, "DEFAULT_PERFIL");
    repository.add(user);
  }

  
}
