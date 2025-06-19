package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.services.EmailService;
import dev.felipemlozx.api_auth.services.UserService;
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
  public ResponseEntity<List<String>> register(@RequestBody CreateUserDto body) throws MessagingException {
      var fails = userService.register(body);
      if (fails.isEmpty()){
        String linkVerify = userService.generateEmailVerify(body.email());
        emailService.sendEmail(body.email(), body.name(), linkVerify);
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
    return ResponseEntity.badRequest().body(fails);
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseLoginDTO> login(@RequestBody LoginDTO body){
    var response = userService.login(body);
    if (response.equals("Email not verify")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseLoginDTO(body.email()));
    }
    return ResponseEntity.ok().body(new ResponseLoginDTO(body.email(), response));
  }

  @GetMapping("/verifyEmail/{id}")
  public ResponseEntity<String> verifyEmail(@PathVariable(name = "id") String token){
    var email = userService.verifyEmailToken(token);
    return ResponseEntity.ok().body(email);
  }

}
