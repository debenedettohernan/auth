package com.Auth.Autenticacion.Service;
import com.Auth.Autenticacion.Entity.Usuario;
import com.Auth.Autenticacion.Enum.Rol;
import com.Auth.Autenticacion.Repository.UsuarioRepository;
import com.Auth.Autenticacion.Security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Auth.Autenticacion.Entity.Password;

@Service
public class AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new RuntimeException("Cuenta inactiva");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword().getHash())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        return jwtUtil.generateToken(username);
    }

    public void registrarUsuario(String username, String password, String email, Rol rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("Usuario ya registrado");
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
    }
}
