package com.Auth.Autenticacion.Controller;

import com.Auth.Autenticacion.Request.LoginRequest;
import com.Auth.Autenticacion.Request.RegisterRequest;
import com.Auth.Autenticacion.Response.AuthResponse;
import com.Auth.Autenticacion.Service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authenticationService.registrarUsuario(request.getUsername(), request.getPassword(), request.getEmail(), request.getRol());
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello World");
    }
}
