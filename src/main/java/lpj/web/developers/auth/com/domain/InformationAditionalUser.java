package lpj.web.developers.auth.com.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record InformationAditionalUser(
        String name,
        String email,
        String password,
        String userRole,
        boolean isActive,
        String dni,
        String telefono) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Lógica para obtener las autoridades/roles del usuario
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userRole)); // Reemplaza esto con tus roles reales
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Puedes implementar la lógica real según sea necesario
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Puedes implementar la lógica real según sea necesario
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Puedes implementar la lógica real según sea necesario
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}
