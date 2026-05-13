package detubarrio.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
