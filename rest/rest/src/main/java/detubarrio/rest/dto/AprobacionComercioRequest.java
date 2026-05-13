package detubarrio.rest.dto;

import jakarta.validation.constraints.NotNull;

public record AprobacionComercioRequest(
    @NotNull Long comercioId,
    String motivoRechazo
) {
}
