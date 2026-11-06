package com.example.backend.security;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

// Fabrique et vérifie les jetons JWT.
// Un jeton contient : l'id de l'utilisateur (subject), son rôle, sa date de création et d'expiration,
// le tout SIGNÉ avec la clé secrète : si quelqu'un modifie le jeton, la signature ne correspond plus.
@Service
public class JwtService {

    private final SecretKey cle;
    private final long dureeMinutes;

    // La clé vient de la variable d'environnement JWT_SECRET (fichier .env), jamais du code.
    // Elle est stockée en Base64 : on la décode pour obtenir les octets de la clé.
    public JwtService(@Value("${app.jwt.secret}") String cleBase64,
                      @Value("${app.jwt.expiration-minutes}") long dureeMinutes) {
        try {
            this.cle = Keys.hmacShaKeyFor(Decoders.BASE64.decode(cleBase64));
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "JWT_SECRET invalide : il doit être encodé en Base64 et faire au moins 32 octets (voir .env.example)", e);
        }
        this.dureeMinutes = dureeMinutes;
    }

    public String genererJeton(User user) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(dureeMinutes, ChronoUnit.MINUTES)))
                .signWith(cle)
                .compact();
    }

    // Lève une JwtException si le jeton est faux, modifié ou expiré (gérée par JwtInterceptor)
    public UtilisateurConnecte lireJeton(String jeton) {
        Claims contenu = Jwts.parser()
                .verifyWith(cle)
                .build()
                .parseSignedClaims(jeton)
                .getPayload();
        return new UtilisateurConnecte(
                Integer.valueOf(contenu.getSubject()),
                Role.valueOf(contenu.get("role", String.class)));
    }

    public long getDureeMinutes() {
        return dureeMinutes;
    }
}
