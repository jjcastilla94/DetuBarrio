package detubarrio.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResenaRequest(
    @NotBlank @Size(max = 80) String titulo,
    @Size(max = 255) String comentario,
    @Min(1) @Max(5) Integer valoracion,
    @NotBlank @Size(max = 100) String autorNombre,
    @Email @Size(max = 150) String autorEmail
) {
}
