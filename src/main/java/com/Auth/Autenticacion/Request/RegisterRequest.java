package com.Auth.Autenticacion.Request;

import com.Auth.Autenticacion.Enum.Rol;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotNull(message = "El nombre de usuario no puede ser nulo")
    @NotEmpty(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 5, max = 20, message = "El nombre de usuario debe tener entre 5 y 20 caracteres")
    private String username;
    @NotNull(message = "La contraseña no puede ser nula")
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 5, max = 20, message = "La contraseña debe tener entre 5 y 20 caracteres")
    private String password;
    @NotNull(message = "El email no puede ser nulo")
    @NotEmpty(message = "El email no puede estar vacío")
    private String email;
    private Rol rol;
}
