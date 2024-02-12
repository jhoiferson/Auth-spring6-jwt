package lpj.web.developers.auth.com.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import lpj.web.developers.auth.com.domain.InformationAditionalUser;

// Clase que implementa RowMapper para mapear filas de resultados JDBC a objetos de dominio
public class UserRowMapper implements RowMapper<InformationAditionalUser> {

    private static final Logger LOG = LoggerFactory.getLogger(UserRowMapper.class);

    // Método obligatorio de la interfaz RowMapper, realiza el mapeo de una fila a un objeto InformationAditionalUser
    @Override
    public InformationAditionalUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            // Mapear columnas del ResultSet a propiedades de InformationAditionalUser
            return new InformationAditionalUser(
                    rs.getString("name"),       // Nombre
                    rs.getString("email"),      // Correo electrónico
                    rs.getString("password"),   // Contraseña
                    rs.getString("perfil"),       // Rol
                    rs.getBoolean("estado"),    // Estado (activo o no)
                    rs.getString("dni"),        // Número de identificación
                    rs.getString("celular")     // Número de teléfono
            );
        } catch (Exception ex) {
            // Manejar cualquier excepción durante el mapeo
            LOG.error("ERROR! Usuario Session", ex.toString());
            LOG.error("ERROR! Usuario Session", ex);
            ex.printStackTrace();
            return null; // O manejo de errores según tu lógica
        }
    }
}
