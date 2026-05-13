package detubarrio.rest.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.AprobacionSolicitudColaboracionRequest;
import detubarrio.rest.dto.MensajeContactoResponse;
import detubarrio.rest.dto.SolicitudColaboracionResponse;
import detubarrio.rest.service.AdminService;
import detubarrio.rest.repository.MensajeContactoRepository;
import detubarrio.rest.repository.SolicitudColaboracionRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/contacto")
@RequiredArgsConstructor
public class AdminContactoController {

    private final MensajeContactoRepository mensajeContactoRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;
    private final AdminService adminService;

    @GetMapping("/mensajes")
    @RolesAllowed("ADMIN")
    public List<MensajeContactoResponse> listarMensajes(Authentication authentication) {
        validarRolAdmin(authentication);
        return mensajeContactoRepository.findAllByOrderByFechaCreacionDesc().stream()
            .map(mensaje -> new MensajeContactoResponse(
                mensaje.getId(),
                mensaje.getNombre(),
                mensaje.getEmail(),
                mensaje.getAsunto(),
                mensaje.getTipo(),
                mensaje.getMensaje(),
                mensaje.getFechaCreacion()
            ))
            .toList();
    }

    @GetMapping("/colaboraciones")
    @RolesAllowed("ADMIN")
    public List<SolicitudColaboracionResponse> listarColaboraciones(Authentication authentication) {
        validarRolAdmin(authentication);
        return adminService.listarSolicitudesColaboracion();
    }

    @PostMapping("/colaboraciones/aprobar")
    @RolesAllowed("ADMIN")
    public SolicitudColaboracionResponse aprobarColaboracion(Authentication authentication, @Valid @RequestBody AprobacionSolicitudColaboracionRequest request) {
        validarRolAdmin(authentication);
        return adminService.aprobarSolicitudColaboracion(request);
    }

    @PostMapping("/colaboraciones/rechazar")
    @RolesAllowed("ADMIN")
    public SolicitudColaboracionResponse rechazarColaboracion(Authentication authentication, @Valid @RequestBody AprobacionSolicitudColaboracionRequest request) {
        validarRolAdmin(authentication);
        return adminService.rechazarSolicitudColaboracion(request);
    }

    private void validarRolAdmin(Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_ADMIN"::equals);

        if (!isAdmin) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para administradores");
        }
    }
}