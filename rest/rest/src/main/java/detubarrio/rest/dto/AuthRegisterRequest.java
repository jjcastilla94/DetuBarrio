package detubarrio.rest.dto;

import detubarrio.rest.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
    @NotBlank @Size(max = 120) String nombre,
    @Email @NotBlank @Size(max = 150) String email,
    @NotBlank @Size(min = 6, max = 120) String password,
    @NotNull RolUsuario rol,
    Long comercioId,
    // Campos opcionales para crear un nuevo comercio si rol es COMERCIO
    @Size(max = 100) String nombreComercio,
    @Size(max = 255) String descripcionComercio,
    @Size(max = 100) String horario,
    @Size(max = 100) String diasApertura,
    @Size(max = 255) String logo,
    @Size(max = 255) String banner,
    Long categoriaId
) {
}
