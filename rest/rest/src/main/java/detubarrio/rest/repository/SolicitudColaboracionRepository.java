package detubarrio.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.EstadoSolicitudColaboracion;
import detubarrio.rest.model.SolicitudColaboracion;

public interface SolicitudColaboracionRepository extends JpaRepository<SolicitudColaboracion, Long> {
	java.util.List<SolicitudColaboracion> findAllByOrderByFechaCreacionDesc();

	java.util.List<SolicitudColaboracion> findByEstadoOrderByFechaCreacionDesc(EstadoSolicitudColaboracion estado);

	java.util.Optional<SolicitudColaboracion> findTopByEmailComercioIgnoreCaseOrderByFechaCreacionDesc(String emailComercio);
}