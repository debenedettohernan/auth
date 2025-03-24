package com.Auth.Autenticacion.Request;

import com.Auth.Autenticacion.Enum.Rol;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotNull(message = "El nombre de usuario no puede ser nulo")
    @NotEmpty(message = "El nombre de usuario no puede estar vacío")
    private String username;
    @NotNull(message = "La contraseña no puede ser nula")
    @NotEmpty(message = "La contraseña no puede estar vacía")
    private String password;
    @NotNull(message = "El email no puede ser nulo")
    @NotEmpty(message = "El email no puede estar vacío")
    private String email;
    private Rol rol;
}
