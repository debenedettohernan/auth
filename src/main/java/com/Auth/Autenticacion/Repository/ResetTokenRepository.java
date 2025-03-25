package com.Auth.Autenticacion.Repository;

import com.Auth.Autenticacion.Entity.ResetToken;
import com.Auth.Autenticacion.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {

    Optional<ResetToken> findByToken(String token);
    Optional<ResetToken> findByUsuario(Usuario usuario);
}
