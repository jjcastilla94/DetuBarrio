package detubarrio.rest.service;

import detubarrio.rest.dto.AprobacionComercioRequest;
import detubarrio.rest.dto.AprobacionSolicitudColaboracionRequest;
import detubarrio.rest.dto.ComercioPendienteResponse;
import detubarrio.rest.dto.SolicitudColaboracionResponse;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.EstadoComercio;
import detubarrio.rest.model.EstadoSolicitudColaboracion;
import detubarrio.rest.model.SolicitudColaboracion;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.SolicitudColaboracionRepository;
import detubarrio.rest.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ComercioRepository comercioRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ComercioPendienteResponse> listarComerciosPendientes() {
        return comercioRepository.findByEstado(EstadoComercio.PENDIENTE).stream()
                .map(this::toComercioPendienteResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComercioPendienteResponse aprobarComercio(AprobacionComercioRequest request) {
        Comercio comercio = comercioRepository.findById(request.comercioId())
                .orElseThrow(() -> new EntityNotFoundException("Comercio no encontrado"));

        if (comercio.getEstado() != EstadoComercio.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aprobar comercios en estado PENDIENTE");
        }

        comercio.setEstado(EstadoComercio.APROBADO);
        comercio.setMotivoRechazo(null);
        // Aprobación de cuenta NO otorga gestión ni lo hace visible públicamente
        comercio.setGestionAutorizada(false);
        comercio.setMotivoBloqueoGestion(null);
        comercio = comercioRepository.save(comercio);

        return toComercioPendienteResponse(comercio);
    }

    @Transactional
    public ComercioPendienteResponse rechazarComercio(AprobacionComercioRequest request) {
        Comercio comercio = comercioRepository.findById(request.comercioId())
                .orElseThrow(() -> new EntityNotFoundException("Comercio no encontrado"));

        if (comercio.getEstado() != EstadoComercio.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar comercios en estado PENDIENTE");
        }

        comercio.setEstado(EstadoComercio.RECHAZADO);
        comercio.setMotivoRechazo(request.motivoRechazo());
        // Al rechazar asegurar que no pueda gestionarse ni aparecer públicamente
        comercio.setGestionAutorizada(false);
        comercio.setMotivoBloqueoGestion(request.motivoRechazo());
        comercio = comercioRepository.save(comercio);

        return toComercioPendienteResponse(comercio);
    }

    @Transactional(readOnly = true)
    public List<SolicitudColaboracionResponse> listarSolicitudesColaboracion() {
        return solicitudColaboracionRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::toSolicitudColaboracionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SolicitudColaboracionResponse aprobarSolicitudColaboracion(AprobacionSolicitudColaboracionRequest request) {
        SolicitudColaboracion solicitud = solicitudColaboracionRepository.findById(request.solicitudId())
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de colaboración no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitudColaboracion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes en estado PENDIENTE");
        }

        solicitud.setEstado(EstadoSolicitudColaboracion.APROBADA);
        solicitud.setMotivoRechazo(null);
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitud = solicitudColaboracionRepository.save(solicitud);

        actualizarGestionDelComercioPorSolicitud(solicitud, true, null);

        return toSolicitudColaboracionResponse(solicitud);
    }

    @Transactional
    public SolicitudColaboracionResponse rechazarSolicitudColaboracion(AprobacionSolicitudColaboracionRequest request) {
        SolicitudColaboracion solicitud = solicitudColaboracionRepository.findById(request.solicitudId())
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de colaboración no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitudColaboracion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes en estado PENDIENTE");
        }

        solicitud.setEstado(EstadoSolicitudColaboracion.RECHAZADA);
        solicitud.setMotivoRechazo(request.motivoRechazo());
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitud = solicitudColaboracionRepository.save(solicitud);

        actualizarGestionDelComercioPorSolicitud(solicitud, false, solicitud.getMotivoRechazo());

        return toSolicitudColaboracionResponse(solicitud);
    }

    private void actualizarGestionDelComercioPorSolicitud(SolicitudColaboracion solicitud, boolean autorizada, String motivoBloqueo) {
        usuarioRepository.findByEmailIgnoreCase(solicitud.getEmailComercio())
                .map(Usuario::getComercio)
                .ifPresentOrElse(
                        comercio -> actualizarGestionDelComercio(comercio, autorizada, motivoBloqueo),
                        () -> {
                            if (solicitud.getComercioId() != null) {
                                comercioRepository.findById(solicitud.getComercioId())
                                        .ifPresent(comercio -> actualizarGestionDelComercio(comercio, autorizada, motivoBloqueo));
                            }
                        }
                );
    }

    private void actualizarGestionDelComercio(Comercio comercio, boolean autorizada, String motivoBloqueo) {
        comercio.setGestionAutorizada(autorizada);
        comercio.setMotivoBloqueoGestion(motivoBloqueo);
        comercioRepository.save(comercio);
    }

    private void activarGestionDelComercio(Usuario usuario) {
        Comercio comercio = usuario.getComercio();
        if (comercio == null) {
            return;
        }

        comercio.setGestionAutorizada(true);
        comercio.setMotivoBloqueoGestion(null);
        comercioRepository.save(comercio);
    }

    private void bloquearGestionDelComercio(Usuario usuario) {
        Comercio comercio = usuario.getComercio();
        if (comercio == null) {
            return;
        }

        comercio.setGestionAutorizada(false);
        comercio.setMotivoBloqueoGestion("Solicitud de colaboración rechazada por el administrador");
        comercioRepository.save(comercio);
    }

    private SolicitudColaboracionResponse toSolicitudColaboracionResponse(SolicitudColaboracion solicitud) {
        return new SolicitudColaboracionResponse(
                solicitud.getId(),
                solicitud.getNombreComercio(),
                solicitud.getNombreTitular(),
                solicitud.getEmailComercio(),
                solicitud.getTelefonoComercio(),
                solicitud.getCategoria(),
                solicitud.getDescripcion(),
                solicitud.isTerminosAceptados(),
                solicitud.getEstado(),
                solicitud.getMotivoRechazo(),
                solicitud.getFechaCreacion()
        );
    }

    private ComercioPendienteResponse toComercioPendienteResponse(Comercio comercio) {
        String nombreUsuario = comercio.getUsuarioCreador() != null ? comercio.getUsuarioCreador().getNombre() : "N/A";
        String emailUsuario = comercio.getUsuarioCreador() != null ? comercio.getUsuarioCreador().getEmail() : "N/A";
        String categoria = comercio.getCategoria() != null ? comercio.getCategoria().getNombreCategoria() : "N/A";

        return new ComercioPendienteResponse(
                comercio.getId(),
                comercio.getNombreComercio(),
                comercio.getDescripcion(),
                nombreUsuario,
                emailUsuario,
                categoria,
                comercio.getFechaSolicitud()
        );
    }
}
