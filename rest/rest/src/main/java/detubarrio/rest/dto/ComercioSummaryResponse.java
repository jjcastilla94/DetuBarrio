package detubarrio.rest.dto;

public record ComercioSummaryResponse(
    Long id,
    String nombreComercio,
    String descripcion,
    String horario,
    String diasApertura,
    String logo,
    String categoria,
    Double puntuacionMedia,
    Long totalResenas
) {
}
