package detubarrio.rest.dto;

import java.time.LocalDateTime;

public record MensajeContactoResponse(
    Long id,
    String nombre,
    String email,
    String asunto,
    String tipo,
    String mensaje,
    LocalDateTime fechaCreacion
) {
}