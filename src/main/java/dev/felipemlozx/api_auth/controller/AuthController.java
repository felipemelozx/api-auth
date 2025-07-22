package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
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
  public ResponseEntity<ApiResponse<List<String>>> register(@RequestBody CreateUserDto body) throws MessagingException {
      var fails = authService.register(body);
      if (fails.isEmpty()){
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse
                .success("User registered.", List.of("Verification email sent.")));
      }
    return ResponseEntity.badRequest().body(ApiResponse.error(fails));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<ResponseLoginDTO>> login(@RequestBody LoginDTO body){
    ResponseLoginDTO response = authService.login(body);
    if(response != null ){
      ApiResponse<ResponseLoginDTO> apiResponse = ApiResponse.success("User logged in successfully", response);
      return ResponseEntity.ok().body(apiResponse);
    } else {
      ApiResponse<ResponseLoginDTO> apiResponse = ApiResponse.error("User or password is Incorrect", null);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }
  }

  @GetMapping("/verify-email/{id}")
  public ResponseEntity<ApiResponse<String>> verifyEmail(@PathVariable(name = "id") String token){
    boolean isValid = authService.verifyEmailToken(token);
    if(!isValid) return ResponseEntity.badRequest().body(ApiResponse.error("Link invalid."));
    return ResponseEntity.ok().build();
  }
}
