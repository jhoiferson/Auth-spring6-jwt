package lpj.web.developers.auth.com.exceptions;

// Clase de excepción personalizada para representar un error de acceso denegado
public class AccessDeniedException extends RuntimeException {

  // Número de versión para garantizar la consistencia durante la deserialización
  private static final long serialVersionUID = 1L;

  // Constructor que toma un mensaje como parámetro
  public AccessDeniedException(String message) {
    // Llama al constructor de la clase base (RuntimeException) con el mensaje proporcionado
    super(message);
  }
}
