package detubarrio.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.ResenaRequest;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comercios/{comercioId}/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @GetMapping
    public List<ResenaResponse> listarResenas(@PathVariable Long comercioId) {
        return resenaService.listarResenasPorComercio(comercioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponse crearResena(
        @PathVariable Long comercioId,
        @Valid @RequestBody ResenaRequest request
    ) {
        return resenaService.crearResena(comercioId, request);
    }
}
