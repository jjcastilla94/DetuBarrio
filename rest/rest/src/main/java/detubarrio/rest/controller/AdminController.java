package detubarrio.rest.controller;

import detubarrio.rest.dto.AprobacionComercioRequest;
import detubarrio.rest.dto.ComercioPendienteResponse;
import detubarrio.rest.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/comercios-pendientes")
    public List<ComercioPendienteResponse> listarComerciosPendientes() {
        return adminService.listarComerciosPendientes();
    }

    @PostMapping("/comercios/aprobar")
    @ResponseStatus(HttpStatus.OK)
    public ComercioPendienteResponse aprobarComercio(@Valid @RequestBody AprobacionComercioRequest request) {
        return adminService.aprobarComercio(request);
    }

    @PostMapping("/comercios/rechazar")
    @ResponseStatus(HttpStatus.OK)
    public ComercioPendienteResponse rechazarComercio(@Valid @RequestBody AprobacionComercioRequest request) {
        return adminService.rechazarComercio(request);
    }
}
