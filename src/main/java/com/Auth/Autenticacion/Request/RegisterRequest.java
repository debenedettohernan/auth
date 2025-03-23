package com.Auth.Autenticacion.Request;

import com.Auth.Autenticacion.Enum.Rol;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotNull
    @NotEmpty
    private String username;
    private String password;
    private String email;
    private Rol rol;
}
