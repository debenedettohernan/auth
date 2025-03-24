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


@Service
public class AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new GlobalException("Cuenta inactiva");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword().getHash())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        return jwtUtil.generateToken(username);
    }


}
