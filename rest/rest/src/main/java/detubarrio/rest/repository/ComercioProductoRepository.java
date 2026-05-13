package detubarrio.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.ComercioProducto;

public interface ComercioProductoRepository extends JpaRepository<ComercioProducto, Long> {
    List<ComercioProducto> findByComercioId(Long comercioId);

    Optional<ComercioProducto> findByComercioIdAndProductoId(Long comercioId, Long productoId);
}
