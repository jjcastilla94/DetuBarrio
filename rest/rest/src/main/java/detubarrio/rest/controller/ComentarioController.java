package detubarrio.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.ComentarioRequest;
import detubarrio.rest.dto.ResenaRequest;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ResenaService resenaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponse crearComentario(@Valid @RequestBody ComentarioRequest request) {
        ResenaRequest resenaRequest = new ResenaRequest(
            request.titulo(),
            request.comentario(),
            request.valoracion(),
            request.autorNombre(),
            request.autorEmail()
        );

        return resenaService.crearResena(request.comercioId(), resenaRequest);
    }
}
