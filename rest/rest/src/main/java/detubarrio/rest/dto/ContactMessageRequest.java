package detubarrio.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 120) String asunto,
    @NotBlank @Size(max = 40) String tipo,
    @NotBlank @Size(max = 2000) String mensaje
) {
}