package com.Auth.Autenticacion.Controller;

import com.Auth.Autenticacion.Entity.Usuario;
import com.Auth.Autenticacion.Repository.UsuarioRepository;
import com.Auth.Autenticacion.Request.EmailRequest;
import com.Auth.Autenticacion.Request.LoginRequest;
import com.Auth.Autenticacion.Request.RegisterRequest;
import com.Auth.Autenticacion.Request.ResetPasswordRequest;
import com.Auth.Autenticacion.Response.AuthResponse;
import com.Auth.Autenticacion.Service.AuthenticationService;
import com.Auth.Autenticacion.Service.MfaService;
import com.Auth.Autenticacion.Service.PasswordResetService;
import com.Auth.Autenticacion.Service.RegisterService;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RegisterService registerService;
    private final PasswordResetService passwordResetService;
    private final MfaService mfaService;
    private final UsuarioRepository usuarioRepository;


    public AuthenticationController(AuthenticationService authenticationService, RegisterService registerService, PasswordResetService passwordResetService, UsuarioRepository usuarioRepository, MfaService mfaService) {
        this.authenticationService = authenticationService;
        this.registerService = registerService;
        this.passwordResetService = passwordResetService;
        this.mfaService = mfaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword(), request.getMfaCode());
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        boolean isValid = mfaService.isValid(usuario.getMfaSecret(), request.getMfaCode());
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código TOTP inválido.");
        }
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request )  {
        registerService.registrarUsuario(request.getUsername(), request.getPassword(), request.getEmail(), request.getRol()) ;
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello World");

    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> solicitarResetPassword(@RequestBody EmailRequest request) {
        passwordResetService.solicitarResetPassword(request.getEmail());
        return ResponseEntity.ok("Se ha enviado un email con instrucciones para restablecer la contraseña.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.cambiarPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok("Contraseña cambiada correctamente.");
    }
    @GetMapping("/unlock")
    public String desbloquearCuenta(@RequestParam String email) {
        return authenticationService.desbloquearCuenta(email);
    }

    @PostMapping("/enable-mfa")
    public ResponseEntity<?> activarMfa(@RequestParam String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String key = mfaService.generarCodigoSecreto();
        usuario.setMfaSecret(key);
        usuarioRepository.save(usuario);

        String qrCodeUrl = mfaService.generarQrParaUsuario(usuario.getUsername(), key);
        return ResponseEntity.ok("Escanea este código QR en Google Authenticator: " + qrCodeUrl);
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<?> verifyMfa(@RequestParam String username, @RequestParam int codigo) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isCodeValid = mfaService.verificarCodigo(usuario.getMfaSecret(), codigo);
        if (isCodeValid) {
            return ResponseEntity.ok("Código MFA verificado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código MFA inválido.");
        }
    }

}
