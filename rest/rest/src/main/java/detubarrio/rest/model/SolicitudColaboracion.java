package detubarrio.rest.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_colaboracion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudColaboracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_colaboracion")
    private Long id;

    @Column(name = "nombre_comercio", nullable = false, length = 120)
    private String nombreComercio;

    @Column(name = "nombre_titular", nullable = false, length = 120)
    private String nombreTitular;

    @Column(name = "email_comercio", nullable = false, length = 150)
    private String emailComercio;

    @Column(name = "telefono_comercio", nullable = false, length = 30)
    private String telefonoComercio;

    @Column(name = "categoria", nullable = false, length = 80)
    private String categoria;

    @Column(name = "descripcion", length = 3000)
    private String descripcion;

    @Column(name = "id_comercio_origen")
    private Long comercioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoSolicitudColaboracion estado = EstadoSolicitudColaboracion.PENDIENTE;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "terminos_aceptados", nullable = false)
    private boolean terminosAceptados;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}