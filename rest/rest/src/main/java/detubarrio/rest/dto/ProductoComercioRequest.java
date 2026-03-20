package detubarrio.rest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoComercioRequest(
    @Size(max = 100) String nombreProducto,
    @Size(max = 255) String descripcion,
    @Size(max = 255) String imagen,
    @NotNull @DecimalMin("0.00") BigDecimal precio,
    @NotNull @Min(0) Integer stock
) {
}
