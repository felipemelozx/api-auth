package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.core.AuthCheckFailure;
import dev.felipemlozx.api_auth.core.AuthCheckResult;
import dev.felipemlozx.api_auth.core.AuthCheckSuccess;
import dev.felipemlozx.api_auth.core.LoginFailure;
import dev.felipemlozx.api_auth.core.LoginResult;
import dev.felipemlozx.api_auth.core.LoginSuccess;
import dev.felipemlozx.api_auth.dto.CreateUserDto;
import dev.felipemlozx.api_auth.dto.LoginDTO;
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

  public List<String> register(CreateUserDto body) throws MessagingException {
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

    String accessToken = tokenService.generateToken(user.getEmail(), null);
    String refreshToken = tokenService.generateToken(user.getEmail(), null);

    return new LoginSuccess(accessToken, refreshToken);
  }

  public boolean verifyEmailToken(String token) {
     return userService.verifyEmailToken(token);
  }
}
