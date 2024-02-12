package lpj.web.developers.auth.com.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lpj.web.developers.auth.com.domain.User;
import lpj.web.developers.auth.com.exceptions.NotFoundException;
import lpj.web.developers.auth.com.exceptions.UserNotActiveException;

@Repository
public class UserRepository {

  // Consulta SQL para insertar un nuevo usuario en la base de datos
  private static final String INSERT = "INSERT INTO crud.user (name, email, password) VALUES(:name, :email, :password)";

  // Consulta SQL para buscar un usuario por su dirección de correo electrónico, considerando el estado = 1
  private static final String FIND_BY_EMAIL = "SELECT * FROM crud.user WHERE email = :email";

  private final JdbcClient jdbcClient;

  // Constructor que recibe una instancia de JdbcClient como dependencia
  public UserRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  // Método para agregar un nuevo usuario a la base de datos
  public void add(User user) {
    // Ejecuta la consulta SQL de inserción con los parámetros proporcionados por el usuario
    long affected = jdbcClient.sql(INSERT)
        .param("name", user.name())
        .param("email", user.email())
        .param("password", user.password())
        .update();

    // Validación mejorada con mensaje de error detallado en caso de fallo en la inserción
    if (affected != 1) {
      throw new IllegalStateException("Error al agregar usuario: " + user);
    }
  }

  /**
   * Busca un usuario por su dirección de correo electrónico y estado activo.
   *
   * @param email La dirección de correo electrónico del usuario.
   * @return Un objeto User si se encuentra.
   * @throws UserNotActiveException Si el usuario no está activo.
   */

  
  public Optional<User> findByEmail(String email) {
	    return jdbcClient.sql(FIND_BY_EMAIL)
	        .param("email", email)
	        .query(User.class)
	        .optional();
	  }
}
