package detubarrio.rest.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import detubarrio.rest.dto.AuthLoginRequest;
import detubarrio.rest.dto.AuthRegisterRequest;
import detubarrio.rest.dto.AuthResponse;
import detubarrio.rest.dto.UsuarioMeResponse;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.RolUsuario;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ComercioRepository comercioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Comercio comercio = null;
        if (request.rol() == RolUsuario.COMERCIO && request.comercioId() != null) {
            comercio = comercioRepository.findById(request.comercioId())
                .orElseThrow(() -> new EntityNotFoundException("No existe el comercio con id " + request.comercioId()));
        }

        Usuario usuario = Usuario.builder()
            .nombre(request.nombre())
            .email(request.email().trim().toLowerCase())
            .passwordHash(passwordEncoder.encode(request.password()))
            .rol(request.rol())
            .comercio(comercio)
            .build();

        usuario = usuarioRepository.save(usuario);
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
