package detubarrio.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "comercio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comercio")
    private Long id;

    @Column(name = "nombre_comercio", nullable = false, length = 100)
    private String nombreComercio;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "horario", length = 100)
    private String horario;

    @Column(name = "dias_apertura", length = 100)
    private String diasApertura;

    @Column(name = "logo", length = 255)
    private String logo;

    @Column(name = "banner", length = 255)
    private String banner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "comercio", fetch = FetchType.LAZY)
    private List<ComercioProducto> comercioProductos;

    @OneToMany(mappedBy = "comercio", fetch = FetchType.LAZY)
    private List<Resena> resenas;
}
