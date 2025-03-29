package com.Auth.Autenticacion.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(mensaje);
        mailSender.send(mailMessage);
    }

    public void enviarCorreoDesbloqueo(String email) {
        String asunto = "Cuenta bloqueada - Restablecer acceso";
        String mensaje = "<p>Su cuenta ha sido bloqueada por múltiples intentos fallidos.</p>"
                + "<p>Haga clic en el siguiente enlace para desbloquear su cuenta:</p>"
                + "<a href='http://localhost:8080/auth/unlock?email=" + email + "'>Desbloquear Cuenta</a>";

        enviarCorreo(email, asunto, mensaje);
    }

    private void enviarCorreo(String destinatario, String asunto, String contenido) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando el email", e);
        }
    }
}
