package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.services.EmailService;
import dev.felipemlozx.api_auth.services.UserService;
import dev.felipemlozx.api_auth.utils.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final EmailService emailService;

  public AuthController(UserService userService, EmailService emailService) {
    this.userService = userService;
    this.emailService = emailService;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<List<String>>> register(@RequestBody CreateUserDto body) throws MessagingException {
      var fails = userService.register(body);
      if (fails.isEmpty()){
        String linkVerify = userService.generateEmailVerify(body.email());
        emailService.sendEmail(body.email(), body.name(), linkVerify);
        return ResponseEntity
            .status(HttpStatus.CREATED).
            body(ApiResponse
                .success("User registered. Verification email sent.", null));
      }
    return ResponseEntity.badRequest().body(ApiResponse.error(fails));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<ResponseLoginDTO>> login(@RequestBody LoginDTO body){
    var response = userService.login(body);

    return ResponseEntity
        .ok()
        .body(ApiResponse
         .success("User logged in successfully", new ResponseLoginDTO(body.email(), response)));
  }

  @GetMapping("/verifyEmail/{id}")
  public ResponseEntity<ApiResponse<String>> verifyEmail(@PathVariable(name = "id") String token){
    var email = userService.verifyEmailToken(token);
    return ResponseEntity.ok().body(ApiResponse.success(email));
  }
}
