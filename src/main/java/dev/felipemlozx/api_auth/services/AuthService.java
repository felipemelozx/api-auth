package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.infra.security.TokenService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
      //emailService.sendEmail(body.email(), body.name(), generateLinkToVerifyEmail(token));
    }
    return result;
  }

  protected String generateLinkToVerifyEmail(String token){
    return this.apiUrl + "/verify-email/" + token;
  }

  public ResponseLoginDTO login(LoginDTO request) {
    boolean authrization = userService.login(request);
    if(!authrization) return null;

    String accessToken = tokenService.generateToken(request.email());
    String refreshToken = tokenService.generateToken(request.email());

    return new ResponseLoginDTO(accessToken, refreshToken);
  }

  public boolean verifyEmailToken(String token) {
     return userService.verifyEmailToken(token);
  }
}
