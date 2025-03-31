package com.Auth.Autenticacion.Service;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;


@Service
public class MfaService {
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generarCodigoSecreto() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String generarQrParaUsuario(String username, GoogleAuthenticatorKey key) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("MiAplicacion", username, key);
    }

    public boolean verificarCodigo(String secret, int codigo) {
        return gAuth.authorize(secret, codigo);
    }
}

