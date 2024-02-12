package lpj.web.developers.auth.com.exceptions;

// Clase de excepción para representar un error de duplicado
public class DuplicateException extends RuntimeException {

    // Identificador para la serialización
    private static final long serialVersionUID = 1L;

    // Constructor que toma un mensaje como parámetro
    public DuplicateException(String message) {
        super(message);
    }
}
