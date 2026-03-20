package detubarrio.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
    @NotBlank @Size(max = 100) String nombreCategoria,
    @Size(max = 255) String descripcion
) {
}
