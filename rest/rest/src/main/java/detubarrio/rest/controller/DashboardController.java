package detubarrio.rest.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.RolUsuario;
import detubarrio.rest.model.EstadoComercio;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.repository.SolicitudColaboracionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final ComercioRepository comercioRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;

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

        Comercio comercio = usuario.getComercio();
        String nombreComercio = comercio != null ? comercio.getNombreComercio() : "Sin comercio asignado";
        String estadoComercio = comercio != null ? comercio.getEstado().name() : "PENDIENTE";
        boolean gestionAutorizada = comercio != null && comercio.isGestionAutorizada();
        String motivoRechazo = comercio != null ? comercio.getMotivoRechazo() : null;
        String motivoBloqueoGestion = comercio != null ? comercio.getMotivoBloqueoGestion() : null;
        var solicitudColaboracion = usuario.getEmail() != null
            ? solicitudColaboracionRepository.findTopByEmailComercioIgnoreCaseOrderByFechaCreacionDesc(usuario.getEmail()).orElse(null)
            : null;

        return Map.of(
            "nombre", usuario.getNombre(),
            "rol", usuario.getRol().name(),
            "email", usuario.getEmail(),
            "comercioNombre", nombreComercio,
            "estadoComercio", estadoComercio,
            "gestionAutorizada", gestionAutorizada,
            "motivoRechazo", motivoRechazo == null ? "" : motivoRechazo,
            "motivoBloqueoGestion", motivoBloqueoGestion == null ? "" : motivoBloqueoGestion,
            "solicitudColaboracionEstado", solicitudColaboracion != null ? solicitudColaboracion.getEstado().name() : "SIN_SOLICITUD",
            "solicitudColaboracionMotivo", solicitudColaboracion != null && solicitudColaboracion.getMotivoRechazo() != null ? solicitudColaboracion.getMotivoRechazo() : ""
        );
    }

    @DeleteMapping("/comercio")
    @Transactional
    public Map<String, String> eliminarComercioRechazado(Authentication authentication) {
        Usuario usuario = loadCurrentUser(authentication.getName());

        if (usuario.getRol() != RolUsuario.COMERCIO) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para comercios");
        }

        Comercio comercio = usuario.getComercio();
        if (comercio == null || comercio.getEstado() != EstadoComercio.RECHAZADO) {
            throw new IllegalStateException("Solo se puede eliminar un comercio rechazado");
        }

        comercio.setUsuarioCreador(null);
        comercioRepository.save(comercio);

        usuario.setComercio(null);
        usuarioRepository.save(usuario);

        comercioRepository.delete(comercio);
        usuarioRepository.delete(usuario);

        return Map.of("message", "Comercio y usuario eliminados correctamente");
    }
}
