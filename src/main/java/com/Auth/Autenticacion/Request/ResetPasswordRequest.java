package com.Auth.Autenticacion.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String nuevaPassword;
}
