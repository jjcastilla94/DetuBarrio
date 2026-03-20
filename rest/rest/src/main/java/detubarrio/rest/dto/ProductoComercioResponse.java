package detubarrio.rest.dto;

import java.math.BigDecimal;

public record ProductoComercioResponse(
    Long productoId,
    String nombreProducto,
    String descripcion,
    BigDecimal precio,
    Integer stock,
    String imagen
) {
}
