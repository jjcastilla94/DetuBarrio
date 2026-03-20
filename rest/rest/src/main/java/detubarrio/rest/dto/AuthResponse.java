package detubarrio.rest.dto;

import detubarrio.rest.model.RolUsuario;

public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String nombre,
    String email,
    RolUsuario rol,
    Long comercioId
) {
}
