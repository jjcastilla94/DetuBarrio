package detubarrio.rest.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CollaborationRequest(
    @NotBlank @Size(max = 120) String nombreComercio,
    @NotBlank @Size(max = 120) String nombreTitular,
    @NotBlank @Email @Size(max = 150) String emailComercio,
    @NotBlank @Size(max = 30) String telefonoComercio,
    @NotBlank @Size(max = 80) String categoria,
    @Size(max = 3000) String descripcion,
    @AssertTrue(message = "Debes aceptar los términos y condiciones") boolean terminos
) {
}