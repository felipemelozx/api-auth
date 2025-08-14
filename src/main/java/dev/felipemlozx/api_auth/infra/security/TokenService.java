package dev.felipemlozx.api_auth.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {
  @Value("${api.secret.key}")
  private String secret;


  public String generateToken(String email, List<String> roles) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("API-AUTH")
                .withSubject(email)
                .withClaim("roles", roles != null && !roles.isEmpty() ? roles : List.of("USER"))
                .withIssuedAt(new Date())
                .withExpiresAt(getExpires())
                .sign(algorithm);

    } catch (JWTCreationException e) {
        throw new IllegalStateException("Error while generating token", e);
    }
}

  public DecodedJWT validateToken(String token) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
                .withIssuer("API-AUTH")
                .build()
                .verify(token);

    } catch (JWTVerificationException e) {
        return null; // token inválido ou expirado
    }
}

  private Instant getExpires() {
    return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.ofHours(-3));
  }
}
