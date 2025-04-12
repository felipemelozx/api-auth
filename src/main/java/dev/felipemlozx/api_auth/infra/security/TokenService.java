package dev.felipemlozx.api_auth.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {
  @Value("${api.secret.key}")
  private String secret;


  public String generateToken(String email){
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      String token = JWT.create()
          .withIssuer("API-AUTH")
          .withSubject(email)
          .withExpiresAt(getExpires())
          .sign(algorithm);
      return token;
    } catch (JWTCreationException e){
      throw new RuntimeException("Error while authenticating.");
    }
  }
  public String validateToken(String token){
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer("API-AUTH")
          .build()
          .verify(token)
          .getSubject();
    }catch (JWTVerificationException e){
       return null;
    }
  }

  private Instant getExpires() {
    return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.ofHours(-3));
  }
}
