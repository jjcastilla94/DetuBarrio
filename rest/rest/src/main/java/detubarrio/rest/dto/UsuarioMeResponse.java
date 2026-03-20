package detubarrio.rest.dto;

import detubarrio.rest.model.RolUsuario;

public record UsuarioMeResponse(
    Long userId,
    String nombre,
    String email,
    RolUsuario rol,
    Long comercioId,
    String comercioNombre
) {
}
