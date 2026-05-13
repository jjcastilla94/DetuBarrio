package detubarrio.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AprobacionSolicitudColaboracionRequest(
    @NotNull Long solicitudId,
    @Size(max = 500) String motivoRechazo
) {
}