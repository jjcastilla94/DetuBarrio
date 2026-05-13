package detubarrio.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.EstadoComercio;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    List<Comercio> findByCategoriaId(Long categoriaId);
    
    List<Comercio> findByEstado(EstadoComercio estado);
    
    List<Comercio> findByCategoriaIdAndEstado(Long categoriaId, EstadoComercio estado);

	List<Comercio> findByEstadoAndGestionAutorizadaTrue(EstadoComercio estado);

	List<Comercio> findByCategoriaIdAndEstadoAndGestionAutorizadaTrue(Long categoriaId, EstadoComercio estado);
}
