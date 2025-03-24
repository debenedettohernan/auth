package com.Auth.Autenticacion.Service;

import com.Auth.Autenticacion.Entity.Password;
import com.Auth.Autenticacion.Entity.Usuario;
import com.Auth.Autenticacion.Enum.Rol;
import com.Auth.Autenticacion.Exception.GlobalException;
import com.Auth.Autenticacion.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public RegisterService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(String username, String password, String email, Rol rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new GlobalException("Usuario ya registrado");
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new GlobalException("Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setRol(rol);
        usuario.setActivo(true);

        Password passwordEntity = new Password();
        passwordEntity.setHash(passwordEncoder.encode(password));
        passwordEntity.setUsuario(usuario);
        usuario.setPassword(passwordEntity);

        usuarioRepository.save(usuario);
        logger.info("Usuario registrado: " + username+ "  pwd: "+password);
    }
}
