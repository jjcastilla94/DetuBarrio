package detubarrio.rest.dto;

import java.time.LocalDateTime;

import detubarrio.rest.model.EstadoSolicitudColaboracion;

public record SolicitudColaboracionResponse(
    Long id,
    String nombreComercio,
    String nombreTitular,
    String emailComercio,
    String telefonoComercio,
    String categoria,
    String descripcion,
    boolean terminosAceptados,
    EstadoSolicitudColaboracion estado,
    String motivoRechazo,
    LocalDateTime fechaCreacion
) {
}