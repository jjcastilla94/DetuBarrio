package detubarrio.rest.dto;

import java.time.LocalDateTime;

public record ComercioPendienteResponse(
    Long id,
    String nombreComercio,
    String descripcion,
    String nombreUsuario,
    String emailUsuario,
    String categoria,
    LocalDateTime fechaSolicitud
) {
}
