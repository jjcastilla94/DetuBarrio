package detubarrio.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.Comercio;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    List<Comercio> findByCategoriaId(Long categoriaId);
}
