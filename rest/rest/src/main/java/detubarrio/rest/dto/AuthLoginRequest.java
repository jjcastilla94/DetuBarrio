package detubarrio.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
    @Email @NotBlank @Size(max = 150) String email,
    @NotBlank @Size(min = 6, max = 120) String password
) {
}
