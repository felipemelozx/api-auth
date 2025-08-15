package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.core.AuthError;
import dev.felipemlozx.api_auth.core.LoginFailure;
import dev.felipemlozx.api_auth.core.LoginResult;
import dev.felipemlozx.api_auth.core.LoginSuccess;
import dev.felipemlozx.api_auth.dto.CreateUserDTO;
import dev.felipemlozx.api_auth.dto.LoginDTO;
import dev.felipemlozx.api_auth.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.services.AuthService;
import dev.felipemlozx.api_auth.utils.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<List<String>>> register(@RequestBody CreateUserDTO body) throws MessagingException {
      List<String> response = authService.register(body);
      if (response.isEmpty()){
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse
                .success("User created. Verify your email.", null));
      }
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Validation errors", response));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<ResponseLoginDTO>> login(@RequestBody LoginDTO body){
    LoginResult result = authService.login(body);

    if(result instanceof LoginSuccess(var accessToken, var refreshToken)) {
      ResponseLoginDTO response = new ResponseLoginDTO(accessToken, refreshToken);
      return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    LoginFailure loginFailure = (LoginFailure) result;

    if(loginFailure.error().equals(AuthError.EMAIL_NOT_VERIFIED)){
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("Email not verified", null));
    }
    if(loginFailure.error().equals(AuthError.INVALID_CREDENTIALS)){
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("User or password is incorrect", null));
    }

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("User not register.", null));
  }

  @GetMapping("/verify-email/{token}")
  public ResponseEntity<ApiResponse<Void>> verifyEmail(@PathVariable String token){
    boolean isValid = authService.verifyEmailToken(token);
    if (isValid) {
      return ResponseEntity.ok(ApiResponse.success("Email verified", null));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Invalid or expired token", null));
  }
}
