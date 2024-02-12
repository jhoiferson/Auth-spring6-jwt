package lpj.web.developers.auth.com.domain;


// La clase representa un usuario en el sistema
public record User(
		String name, 
		String email,
		String password, 
		String perfil) {
	
	 // Método para verificar si el usuario está activo
    public boolean isActive() {
        // Lógica para determinar si el usuario está activo
        return true; // Cambia esto según tus requisitos
    }

}
