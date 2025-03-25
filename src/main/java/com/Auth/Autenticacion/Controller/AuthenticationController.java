package com.Auth.Autenticacion.Controller;

import com.Auth.Autenticacion.Request.EmailRequest;
import com.Auth.Autenticacion.Request.LoginRequest;
import com.Auth.Autenticacion.Request.RegisterRequest;
import com.Auth.Autenticacion.Request.ResetPasswordRequest;
import com.Auth.Autenticacion.Response.AuthResponse;
import com.Auth.Autenticacion.Service.AuthenticationService;
import com.Auth.Autenticacion.Service.PasswordResetService;
import com.Auth.Autenticacion.Service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RegisterService registerService;
    private final PasswordResetService passwordResetService;

    public AuthenticationController(AuthenticationService authenticationService, RegisterService registerService, PasswordResetService passwordResetService) {
        this.authenticationService = authenticationService;
        this.registerService = registerService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword());
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
}
