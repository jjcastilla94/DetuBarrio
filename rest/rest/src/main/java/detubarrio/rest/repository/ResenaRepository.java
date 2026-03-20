package detubarrio.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import detubarrio.rest.model.Resena;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByComercioIdOrderByFechaDesc(Long comercioId);

    long countByComercioId(Long comercioId);

    @Query("select coalesce(avg(r.valoracion), 0) from Resena r where r.comercio.id = :comercioId")
    Double findAverageValoracionByComercioId(@Param("comercioId") Long comercioId);
}
