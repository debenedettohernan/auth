package com.Auth.Autenticacion.Request;

import com.Auth.Autenticacion.Enum.Rol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Rol rol;
}
