package detubarrio.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComercioRequest(
    @NotBlank @Size(max = 100) String nombreComercio,
    @Size(max = 255) String descripcion,
    @Size(max = 100) String horario,
    @Size(max = 100) String diasApertura,
    @Size(max = 255) String logo,
    @Size(max = 255) String banner,
    @NotNull Long categoriaId
) {
}
