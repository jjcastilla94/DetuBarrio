package detubarrio.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
