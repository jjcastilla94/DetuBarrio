package detubarrio.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.ComercioDetailResponse;
import detubarrio.rest.dto.ComercioRequest;
import detubarrio.rest.dto.ComercioSummaryResponse;
import detubarrio.rest.dto.ProductoComercioRequest;
import detubarrio.rest.dto.ProductoComercioResponse;
import detubarrio.rest.service.ComercioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comercios")
@RequiredArgsConstructor
public class ComercioController {

    private final ComercioService comercioService;

    @GetMapping
    public List<ComercioSummaryResponse> listarComercios(@RequestParam Optional<Long> categoriaId) {
        return comercioService.listarComercios(categoriaId);
    }

    @GetMapping("/{comercioId}")
    public ComercioDetailResponse obtenerComercio(@PathVariable Long comercioId) {
        return comercioService.obtenerComercio(comercioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComercioSummaryResponse crearComercio(@Valid @RequestBody ComercioRequest request) {
        return comercioService.crearComercio(request);
    }

    @GetMapping("/{comercioId}/productos")
    public List<ProductoComercioResponse> listarProductosComercio(@PathVariable Long comercioId) {
        return comercioService.obtenerProductosComercio(comercioId);
    }

    @PostMapping("/{comercioId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoComercioResponse agregarProductoAComercio(
        @PathVariable Long comercioId,
        @Valid @RequestBody ProductoComercioRequest request
    ) {
        return comercioService.agregarProductoAComercio(comercioId, request);
    }
}
