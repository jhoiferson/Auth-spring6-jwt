package lpj.web.developers.auth.com.exceptions;

// Clase para representar una respuesta de error en la API
public class ApiErrorResponse {

    // Mensaje de error
    private final String message;

    // Constructor que toma un mensaje como parámetro
    public ApiErrorResponse(String message) {
        this.message = message;
    }

    // Método para obtener el mensaje de error
    public String getMessage() {
        return message;
    }
}
