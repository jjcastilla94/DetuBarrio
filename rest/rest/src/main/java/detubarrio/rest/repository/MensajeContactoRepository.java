package detubarrio.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.MensajeContacto;

public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {
	java.util.List<MensajeContacto> findAllByOrderByFechaCreacionDesc();
}