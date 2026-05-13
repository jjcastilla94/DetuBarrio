package detubarrio.rest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import detubarrio.rest.dto.CollaborationRequest;
import detubarrio.rest.dto.ContactMessageRequest;
import detubarrio.rest.dto.ContactSubmissionResponse;
import detubarrio.rest.model.MensajeContacto;
import detubarrio.rest.model.SolicitudColaboracion;
import detubarrio.rest.repository.MensajeContactoRepository;
import detubarrio.rest.repository.SolicitudColaboracionRepository;

@Service
public class ContactoService {

    private static final Logger log = LoggerFactory.getLogger(ContactoService.class);

    private final MensajeContactoRepository mensajeContactoRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String adminEmail;
    private final String fromEmail;

    public ContactoService(
        MensajeContactoRepository mensajeContactoRepository,
        SolicitudColaboracionRepository solicitudColaboracionRepository,
        ObjectProvider<JavaMailSender> mailSenderProvider,
        @Value("${app.contact.admin-email:}") String adminEmail,
        @Value("${app.contact.from-email:no-reply@detubarrio.local}") String fromEmail
    ) {
        this.mensajeContactoRepository = mensajeContactoRepository;
        this.solicitudColaboracionRepository = solicitudColaboracionRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.adminEmail = adminEmail;
        this.fromEmail = fromEmail;
    }

    @Transactional
    public ContactSubmissionResponse enviarMensaje(ContactMessageRequest request) {
        MensajeContacto mensaje = MensajeContacto.builder()
            .nombre(request.nombre().trim())
            .email(request.email().trim().toLowerCase())
            .asunto(request.asunto().trim())
            .tipo(request.tipo().trim())
            .mensaje(request.mensaje().trim())
            .build();

        mensajeContactoRepository.save(mensaje);
        enviarNotificacionContacto(mensaje);

        return new ContactSubmissionResponse(
            "¡Gracias por tu mensaje! Nos pondremos en contacto contigo lo antes posible."
        );
    }

    @Transactional
    public ContactSubmissionResponse solicitarColaboracion(CollaborationRequest request) {
        SolicitudColaboracion solicitud = SolicitudColaboracion.builder()
            .nombreComercio(request.nombreComercio().trim())
            .nombreTitular(request.nombreTitular().trim())
            .emailComercio(request.emailComercio().trim().toLowerCase())
            .telefonoComercio(request.telefonoComercio().trim())
            .categoria(request.categoria().trim())
            .descripcion(request.descripcion() != null ? request.descripcion().trim() : null)
            .terminosAceptados(request.terminos())
            .build();

        solicitudColaboracionRepository.save(solicitud);
        enviarNotificacionColaboracion(solicitud);

        return new ContactSubmissionResponse(
            "¡Gracias por tu solicitud! Revisaremos tu comercio y te contactaremos en breve."
        );
    }

    private void enviarNotificacionContacto(MensajeContacto mensaje) {
        enviarCorreoSiEsPosible(
            "Nuevo mensaje de contacto",
            String.join(
                "\n",
                "Has recibido un nuevo mensaje de contacto:",
                "",
                "Nombre: " + mensaje.getNombre(),
                "Email: " + mensaje.getEmail(),
                "Asunto: " + mensaje.getAsunto(),
                "Tipo: " + mensaje.getTipo(),
                "",
                "Mensaje:",
                mensaje.getMensaje()
            )
        );
    }

    private void enviarNotificacionColaboracion(SolicitudColaboracion solicitud) {
        enviarCorreoSiEsPosible(
            "Nueva solicitud de colaboracion",
            String.join(
                "\n",
                "Has recibido una nueva solicitud de colaboracion:",
                "",
                "Comercio: " + solicitud.getNombreComercio(),
                "Titular: " + solicitud.getNombreTitular(),
                "Email: " + solicitud.getEmailComercio(),
                "Telefono: " + solicitud.getTelefonoComercio(),
                "Categoria: " + solicitud.getCategoria(),
                "",
                "Descripcion:",
                solicitud.getDescripcion() != null ? solicitud.getDescripcion() : "-"
            )
        );
    }

    private void enviarCorreoSiEsPosible(String subject, String body) {
        if (adminEmail == null || adminEmail.isBlank()) {
            log.info("Correo omitido porque no hay destinatario configurado para la administracion");
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("Correo omitido porque no hay JavaMailSender configurado");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setFrom(fromEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("No se pudo enviar el correo de notificacion: {}", ex.getMessage());
        }
    }
}