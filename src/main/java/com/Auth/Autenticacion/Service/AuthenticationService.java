package com.Auth.Autenticacion.Service;
import com.Auth.Autenticacion.Exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Auth.Autenticacion.Entity.Usuario;
import com.Auth.Autenticacion.Enum.Rol;
import com.Auth.Autenticacion.Repository.UsuarioRepository;
import com.Auth.Autenticacion.Security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Auth.Autenticacion.Entity.Password;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthenticationService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    public String authenticate(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.isBloqueado()) {
            if (usuario.getDesbloqueoHora() != null && usuario.getDesbloqueoHora().isBefore(LocalDateTime.now())) {
                usuario.setBloqueado(false);
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
            } else {
                throw new RuntimeException("Cuenta bloqueada. Intente más tarde.");
            }
        }

        if (!passwordEncoder.matches(password, usuario.getPassword().getHash())) {
            registrarIntentoFallido(usuario);
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        usuario.setIntentosFallidos(0); // Reiniciar intentos en caso de éxito
        usuarioRepository.save(usuario);

        return jwtUtil.generateToken(username);
    }

    private void registrarIntentoFallido(Usuario usuario) {
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);

        if (intentos >= 5) {
            usuario.setBloqueado(true);
            usuario.setDesbloqueoHora(LocalDateTime.now().plusMinutes(15)); // Se desbloqueará en 15 min
            usuarioRepository.save(usuario);
            emailService.enviarCorreoDesbloqueo(usuario.getEmail());
        } else {
            usuarioRepository.save(usuario);
        }
    }
    public String desbloquearCuenta(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setBloqueado(false);
            usuario.setIntentosFallidos(0);
            usuario.setDesbloqueoHora(null);
            usuarioRepository.save(usuario);
            return "Cuenta desbloqueada correctamente. Ya puede iniciar sesión.";
        } else {
            return "Usuario no encontrado.";
        }
    }
}