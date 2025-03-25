package com.Auth.Autenticacion.Service;

import com.Auth.Autenticacion.Entity.ResetToken;
import com.Auth.Autenticacion.Entity.Usuario;
import com.Auth.Autenticacion.Repository.ResetTokenRepository;
import com.Auth.Autenticacion.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UsuarioRepository usuarioRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(UsuarioRepository usuarioRepository, ResetTokenRepository resetTokenRepository,
                                PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void solicitarResetPassword(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Generar token
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(15)); // Expira en 15 min

        // Guardar el token en la base de datos
        resetTokenRepository.findByUsuario(usuario).ifPresent(resetTokenRepository::delete);
        resetTokenRepository.save(resetToken);

        // Enviar email con el token
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
        emailService.enviarEmail(email, "Recuperación de contraseña",
                "Para restablecer tu contraseña, haz clic en el siguiente enlace: " + resetLink);
    }

    public void cambiarPassword(String token, String nuevaPassword) {
        ResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.getPassword().setHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Eliminar token después de su uso
        resetTokenRepository.delete(resetToken);
    }
}
