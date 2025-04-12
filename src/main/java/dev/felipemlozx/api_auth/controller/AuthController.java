package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.services.EmailService;
import dev.felipemlozx.api_auth.services.UserService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final EmailService emailService;
  public AuthController(UserService repository, PasswordEncoder passwordEncoder, EmailService emailService) {
    this.userService = repository;
    this.emailService = emailService;
  }

  @PostMapping("/register")
  public ResponseEntity<List<String>> register(@RequestBody CreateUserDto body) throws MessagingException {
      var fails = userService.register(body);
      if (fails.isEmpty()){
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(body.email()).toUri();
        String linkVerify = userService.generateEmailVerify(body.email());
        emailService.sendEmail(body.email(), body.name(), linkVerify);
        return ResponseEntity.created(location).build();
      }
    return ResponseEntity.badRequest().body(fails);
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseLoginDTO> login(@RequestBody LoginDTO body){
    var response = userService.login(body);
    if (response == "Email not verify") {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseLoginDTO(body.email()));
    }
    if(response != null){
      return ResponseEntity.ok().body(new ResponseLoginDTO(body.email(), response));
    }
    return null;
  }

  @GetMapping("/verifyEmail/{id}")
  public ResponseEntity<String> verifyEmail(@PathVariable(name = "id") String token){
    var email = userService.verifyEmailToken(token);
    return ResponseEntity.ok().body(email);
  }

}
