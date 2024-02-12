package lpj.web.developers.auth.com.repository;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import jakarta.transaction.Transactional;

import java.util.Map;

import lpj.web.developers.auth.com.controller.dto.LoginRequest;
import lpj.web.developers.auth.com.domain.InformationAditionalUser;
import lpj.web.developers.auth.com.domain.LoginAttempt;
import lpj.web.developers.auth.com.domain.User;
import lpj.web.developers.auth.com.exceptions.UserBlockedException;
import lpj.web.developers.auth.com.models.mappers.UserRowMapper;


import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;


// Repositorio para manejar los intentos de inicio de sesión en la base de datos
@Repository
public class LoginAttemptRepository {
	

  private static final int RECENT_COUNT = 5; // Puede estar en la configuración
  private static final String INSERT = "INSERT INTO crud.login_attempt (email, success, created_at) VALUES(:email, :success, :createdAt)";
  private static final String FIND_RECENT = "SELECT * FROM crud.login_attempt WHERE email = :email ORDER BY created_at DESC LIMIT :recentCount";
  private static final String INCREMENT_FAILED_ATTEMPTS = "UPDATE crud.login_attempt SET failed_attempts = failed_attempts + 1 WHERE email = :email";
  private static final String UPDATE_STATUS_USER = "UPDATE crud.user set estado = '0' WHERE email = :email";

  private final JdbcClient jdbcClient;

  // Constructor que recibe un cliente JDBC para interactuar con la base de datos
  public LoginAttemptRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  // Método para agregar un intento de inicio de sesión a la base de datos
  public void add(LoginAttempt loginAttempt) {
      long affected = jdbcClient.sql(INSERT)
          .param("email", loginAttempt.email())
          .param("success", loginAttempt.success())
          .param("createdAt", loginAttempt.createdAt())
          .update();

      incrementFailedAttempts(loginAttempt.email());
      Assert.isTrue(affected == 1, "No se pudo agregar el intento de inicio de sesión.");
  }


  // Método para buscar los intentos de inicio de sesión más recientes para un usuario específico
  public List<LoginAttempt> findRecent(String email) {
    return jdbcClient.sql(FIND_RECENT)
        .param("email", email)
        .param("recentCount", RECENT_COUNT)
        .query(LoginAttempt.class)
        .list();
  }
  

  
  public boolean isUserBlocked(String email) {
	    String sqlQuery = "SELECT la.failed_attempts, u.estado\r\n"
	            + "FROM crud.login_attempt AS la, crud.user AS u\r\n"
	            + "WHERE la.email=u.email\r\n"
	            + "AND u.email = :email";

	    List<Object[]> result = jdbcClient.sql(sqlQuery)
	        .param("email", email)
	        .query(new RowMapper<Object[]>() {
	            @Override
	            public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new Object[]{rs.getInt("failed_attempts"), rs.getInt("estado")};
	            }
	        })
	        .list();

	    if (!result.isEmpty()) {
	        Integer failedAttempts = (Integer) result.get(0)[0];
	        Integer estado = (Integer) result.get(0)[1];

	        System.out.println("failedAttempts: " + failedAttempts);
	        System.out.println("estado: " + estado);
	        return failedAttempts != null && failedAttempts > 3 && estado != null && estado == 0;
	    }
	    return false;
	}


  public void incrementFailedAttempts(String email) {
      jdbcClient.sql(INCREMENT_FAILED_ATTEMPTS)
          .param("email", email)
          .update();
  }

  public void 	updateStatusUser(String email) {
      jdbcClient.sql(UPDATE_STATUS_USER)
          .param("email", email)
          .update();
  }
  
  

  public void resetFailedAttempts(String email) {
	    String sqlQuery = "DELETE FROM crud.login_attempt WHERE email = :email";

	    jdbcClient.sql(sqlQuery)
	        .param("email", email)
	        .update();
	}



//Método para verificar si un usuario tiene un intento registrado
  public boolean isUserRegistered(String email) {
	    String sqlQuery = "SELECT COUNT(*) FROM crud.login_attempt WHERE email = :email";

	    List<Integer> countList = jdbcClient.sql(sqlQuery)
	            .param("email", email)
	            .query(new RowMapper<Integer>() {
	                @Override
	                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    return rs.getInt(1);
	                }
	            })
	            .list();

	    return !countList.isEmpty() && countList.get(0) > 0;
	}

  
  public int getFailedAttempts(String email) {
	    String sqlQuery = "SELECT failed_attempts FROM crud.login_attempt WHERE email = :email";

	    List<Integer> failedAttemptsList = jdbcClient.sql(sqlQuery)
	        .param("email", email)
	        .query(new RowMapper<Integer>() {
	            @Override
	            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt("failed_attempts");
	            }
	        })
	        .list();

	    if (!failedAttemptsList.isEmpty()) {
	        return failedAttemptsList.get(0);
	    }

	    return 0;
	}

  
  public Map<String, Object> getAdditionalUserData(String email) {
	    String sqlQuery = "SELECT * FROM crud.user AS u, crud.persona AS p\r\n"
	    		+ "WHERE \r\n"
	    		+ "u.email=p.email\r\n"
	    		+ "and u.email = :email";

	    List<Map<String, Object>> additionalDataList = jdbcClient.sql(sqlQuery)
	            .param("email", email)
	            .query(new RowMapper<Map<String, Object>>() {
	                @Override
	                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    // Mapear los datos adicionales del usuario desde el ResultSet a un Map
	                    Map<String, Object> additionalData = new HashMap<>();
	                    additionalData.put("nombre", rs.getString("nombres")); // Reemplazar con los nombres de columnas correctos
	                    additionalData.put("email", rs.getObject("email"));
	                    additionalData.put("dni", rs.getObject("dni"));
	                    additionalData.put("cell", rs.getObject("celular"));
	                    additionalData.put("estado", rs.getObject("estado"));
	                    // Agregar otros campos según sea necesario
	                    return additionalData;
	                }
	            })
	            .list();

	    if (!additionalDataList.isEmpty()) {
	        return additionalDataList.get(0); // Obtener el primer elemento si hay resultados
	    }

	    return Collections.emptyMap(); // Devolver un mapa vacío si no hay resultados
	}

  @Autowired
  private JdbcTemplate jdbcTemplate;
  

  

}
