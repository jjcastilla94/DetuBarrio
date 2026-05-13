package detubarrio.rest.dto;

import java.util.List;

public record ComercioDetailResponse(
    Long id,
    String nombreComercio,
    String descripcion,
    String horario,
    String diasApertura,
    String logo,
    String banner,
    String categoria,
    Double puntuacionMedia,
    Long totalResenas,
    List<ProductoComercioResponse> productos,
    List<ResenaResponse> resenas
) {
}
