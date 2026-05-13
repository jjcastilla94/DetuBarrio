package detubarrio.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.CollaborationRequest;
import detubarrio.rest.dto.ContactMessageRequest;
import detubarrio.rest.dto.ContactSubmissionResponse;
import detubarrio.rest.service.ContactoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contacto")
@RequiredArgsConstructor
public class ContactoController {

    private final ContactoService contactoService;

    @PostMapping("/mensaje")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactSubmissionResponse enviarMensaje(@Valid @RequestBody ContactMessageRequest request) {
        return contactoService.enviarMensaje(request);
    }

    @PostMapping("/colaboracion")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactSubmissionResponse solicitarColaboracion(@Valid @RequestBody CollaborationRequest request) {
        return contactoService.solicitarColaboracion(request);
    }
}