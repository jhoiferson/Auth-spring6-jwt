package lpj.web.developers.auth.com.exceptions;

// Clase de excepción para representar un error de no encontrado
public class NotFoundException extends RuntimeException {

    // Identificador para la serialización
    private static final long serialVersionUID = 1L;

    // Constructor que toma un mensaje como parámetro
    public NotFoundException(String message) {
        super(message);
    }
}
