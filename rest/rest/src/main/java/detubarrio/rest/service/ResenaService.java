package detubarrio.rest.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import detubarrio.rest.dto.ResenaRequest;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.Resena;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.ResenaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ComercioRepository comercioRepository;

    public List<ResenaResponse> listarResenasPorComercio(Long comercioId) {
        return resenaRepository.findByComercioIdOrderByFechaDesc(comercioId)
            .stream()
            .map(resena -> new ResenaResponse(
                resena.getId(),
                resena.getTitulo(),
                resena.getComentario(),
                resena.getValoracion(),
                resena.getAutorNombre(),
                resena.getAutorEmail(),
                resena.getFecha()
            ))
            .toList();
    }

    @Transactional
    public ResenaResponse crearResena(Long comercioId, ResenaRequest request) {
        Comercio comercio = comercioRepository.findById(comercioId)
            .orElseThrow(() -> new EntityNotFoundException("No existe el comercio con id " + comercioId));

        Resena resena = Resena.builder()
            .titulo(request.titulo())
            .comentario(request.comentario())
            .valoracion(request.valoracion())
            .autorNombre(request.autorNombre())
            .autorEmail(request.autorEmail())
            .fecha(LocalDateTime.now())
            .comercio(comercio)
            .build();

        Resena guardada = resenaRepository.save(resena);

        return new ResenaResponse(
            guardada.getId(),
            guardada.getTitulo(),
            guardada.getComentario(),
            guardada.getValoracion(),
            guardada.getAutorNombre(),
            guardada.getAutorEmail(),
            guardada.getFecha()
        );
    }
}
