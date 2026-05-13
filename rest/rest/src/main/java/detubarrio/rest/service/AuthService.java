package detubarrio.rest.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import detubarrio.rest.dto.AuthLoginRequest;
import detubarrio.rest.dto.AuthRegisterRequest;
import detubarrio.rest.dto.AuthResponse;
import detubarrio.rest.dto.UsuarioMeResponse;
import detubarrio.rest.model.Categoria;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.EstadoComercio;
import detubarrio.rest.model.EstadoSolicitudColaboracion;
import detubarrio.rest.model.RolUsuario;
import detubarrio.rest.model.SolicitudColaboracion;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.CategoriaRepository;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.SolicitudColaboracionRepository;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ComercioRepository comercioRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Comercio comercio = null;
        
        // Si el rol es COMERCIO, crear un nuevo comercio o vincular a uno existente
        if (request.rol() == RolUsuario.COMERCIO) {
            if (request.comercioId() != null) {
                // Vincular a comercio existente
                comercio = comercioRepository.findById(request.comercioId())
                    .orElseThrow(() -> new EntityNotFoundException("No existe el comercio con id " + request.comercioId()));
            } else if (request.nombreComercio() != null && !request.nombreComercio().isBlank() && request.categoriaId() != null) {
                // Crear nuevo comercio en estado PENDIENTE
                Categoria categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("No existe la categoría con id " + request.categoriaId()));
                
                comercio = Comercio.builder()
                    .nombreComercio(request.nombreComercio())
                    .descripcion(request.descripcionComercio())
                    .horario(request.horario())
                    .diasApertura(request.diasApertura())
                    .logo(request.logo())
                    .banner(request.banner())
                    .categoria(categoria)
                    .estado(EstadoComercio.PENDIENTE)
                    .fechaSolicitud(LocalDateTime.now())
                    .build();
            }
        }

        Usuario usuario = Usuario.builder()
            .nombre(request.nombre())
            .email(request.email().trim().toLowerCase())
            .passwordHash(passwordEncoder.encode(request.password()))
            .rol(request.rol())
            .comercio(comercio)
            .build();

        usuario = usuarioRepository.save(usuario);
        
        // Si se creó un nuevo comercio, asignar el usuario creador
        if (comercio != null && comercio.getId() == null) {
            comercio.setUsuarioCreador(usuario);
            comercio = comercioRepository.save(comercio);
            usuario.setComercio(comercio);
            usuario = usuarioRepository.save(usuario);
        }
        
        String token = jwtService.generateToken(usuario);
        Long comercioId = usuario.getComercio() != null ? usuario.getComercio().getId() : null;

        return new AuthResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            comercioId
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthLoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        if (usuario.getRol() == RolUsuario.COMERCIO && usuario.getEmail() != null) {
            SolicitudColaboracion ultimaSolicitud = solicitudColaboracionRepository
                .findTopByEmailComercioIgnoreCaseOrderByFechaCreacionDesc(usuario.getEmail())
                .orElse(null);

            if (ultimaSolicitud != null && ultimaSolicitud.getEstado() == EstadoSolicitudColaboracion.RECHAZADA) {
                throw new BadCredentialsException("Tu comercio ha sido bloqueado por el administrador");
            }
        }

        String token = jwtService.generateToken(usuario);
        Long comercioId = usuario.getComercio() != null ? usuario.getComercio().getId() : null;

        return new AuthResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            comercioId
        );
    }

    @Transactional(readOnly = true)
    public UsuarioMeResponse meByEmail(String email) {
        Usuario usuario = usuarioRepository.findWithComercioByEmailIgnoreCase(email)
            .orElseThrow(() -> new EntityNotFoundException("No existe el usuario autenticado"));

        String comercioNombre = usuario.getComercio() != null ? usuario.getComercio().getNombreComercio() : null;
        Long comercioId = usuario.getComercio() != null ? usuario.getComercio().getId() : null;

        return new UsuarioMeResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            comercioId,
            comercioNombre
        );
    }
}
