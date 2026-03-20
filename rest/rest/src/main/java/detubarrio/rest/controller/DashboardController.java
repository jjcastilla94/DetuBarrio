package detubarrio.rest.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.model.RolUsuario;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioRepository usuarioRepository;

    private Usuario loadCurrentUser(String email) {
        return usuarioRepository.findWithComercioByEmailIgnoreCase(email)
            .orElseThrow(() -> new EntityNotFoundException("No existe el usuario autenticado"));
    }

    @GetMapping("/usuario")
    public Map<String, Object> usuario(Authentication authentication) {
        Usuario usuario = loadCurrentUser(authentication.getName());

        if (usuario.getRol() != RolUsuario.USUARIO) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para usuarios");
        }

        return Map.of(
            "nombre", usuario.getNombre(),
            "rol", usuario.getRol().name(),
            "email", usuario.getEmail()
        );
    }

    @GetMapping("/comercio")
    public Map<String, Object> comercio(Authentication authentication) {
        Usuario usuario = loadCurrentUser(authentication.getName());

        if (usuario.getRol() != RolUsuario.COMERCIO) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para comercios");
        }

        String nombreComercio = usuario.getComercio() != null ? usuario.getComercio().getNombreComercio() : "Sin comercio asignado";

        return Map.of(
            "nombre", usuario.getNombre(),
            "rol", usuario.getRol().name(),
            "email", usuario.getEmail(),
            "comercioNombre", nombreComercio
        );
    }
}
