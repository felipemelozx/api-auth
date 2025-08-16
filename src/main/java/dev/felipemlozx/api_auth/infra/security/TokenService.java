package dev.felipemlozx.api_auth.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import dev.felipemlozx.api_auth.entity.User;
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
  private static final String ISSUER = "API-auth";


  public String generateToken(User user) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim("id", user.getId())
                .withClaim("name", user.getName())
                .withClaim("email", user.getEmail())
                .withClaim("roles", List.of("USER_ROLE"))
                .withIssuedAt(new Date())
                .withExpiresAt(getExpires())
                .sign(algorithm);

    } catch (JWTCreationException e) {
        throw new IllegalStateException("Error while generating token", e);
    }
  }

  public String generateRefreshToken(User user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.create()
          .withIssuer(ISSUER)
          .withClaim("id", user.getId())
          .withIssuedAt(new Date())
          .withExpiresAt(getRefreshExpires())
          .sign(algorithm);

    } catch (JWTCreationException e) {
      throw new IllegalStateException("Error while generating refresh token", e);
    }
  }

  public DecodedJWT validateToken(String token) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token);

    } catch (JWTVerificationException e) {
        return null; // token inválido ou expirado
    }
}

  private Instant getExpires() {
    return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.ofHours(-3));
  }

  private Instant getRefreshExpires() {
    return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.ofHours(-3));
  }
}
