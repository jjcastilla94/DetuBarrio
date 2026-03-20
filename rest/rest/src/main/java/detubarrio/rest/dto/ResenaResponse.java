package detubarrio.rest.dto;

import java.time.LocalDateTime;

public record ResenaResponse(
    Long id,
    String titulo,
    String comentario,
    Integer valoracion,
    String autorNombre,
    String autorEmail,
    LocalDateTime fecha
) {
}
