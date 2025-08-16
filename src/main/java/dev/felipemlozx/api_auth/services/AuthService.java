package dev.felipemlozx.api_auth.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.felipemlozx.api_auth.core.AuthCheckFailure;
import dev.felipemlozx.api_auth.core.AuthCheckResult;
import dev.felipemlozx.api_auth.core.AuthCheckSuccess;
import dev.felipemlozx.api_auth.core.AuthError;
import dev.felipemlozx.api_auth.core.LoginFailure;
import dev.felipemlozx.api_auth.core.LoginResult;
import dev.felipemlozx.api_auth.core.LoginSuccess;
import dev.felipemlozx.api_auth.dto.CreateUserDTO;
import dev.felipemlozx.api_auth.dto.LoginDTO;
import dev.felipemlozx.api_auth.dto.UserJwtDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.infra.security.TokenService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

  private final UserService userService;
  private final EmailService emailService;
  private final TokenService tokenService;

  @Value("${API_URL}")
  private String apiUrl;

  public AuthService(UserService userService, EmailService emailService, TokenService tokenService) {
    this.userService = userService;
    this.emailService = emailService;
    this.tokenService = tokenService;
  }

  public List<String> register(CreateUserDTO body) throws MessagingException {
    List<String> result = userService.register(body);
    if(result.isEmpty()){
      String token = userService.createEmailVerificationToken(body.email());
      if(token != null ) {
        emailService.sendEmail(body.email(), body.name(), generateLinkToVerifyEmail(token));
      }
    }
    return result;
  }

  protected String generateLinkToVerifyEmail(String token){
    return this.apiUrl + "/verify-email/" + token;
  }

  public LoginResult login(LoginDTO request) {
    AuthCheckResult checkResult = userService.login(request);
    if(checkResult instanceof AuthCheckFailure(var error)) {
      return new LoginFailure(error);
    }

    var success = (AuthCheckSuccess) checkResult;
    var user = success.user();
    String accessToken = tokenService.generateToken(user);
    String refreshToken = tokenService.generateRefreshToken(user);

    return new LoginSuccess(accessToken, refreshToken);
  }

  public boolean verifyEmailToken(String token) {
    return userService.verifyEmailToken(token);
  }

  public LoginResult verifyToken(String refreshToken) {
    DecodedJWT res = tokenService.validateToken(refreshToken);
    if(res == null){
      return new LoginFailure(AuthError.REFRESH_TOKEN_INVALID);
    }
    long userId = res.getClaim("id").asLong();
    User user = userService.findById(userId);
    String newAccessToken = tokenService.generateToken(user);

    return new LoginSuccess(newAccessToken, refreshToken);
  }
}
