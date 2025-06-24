package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import dev.felipemlozx.api_auth.utils.CheckUtils;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import dev.felipemlozx.api_auth.infra.security.TokenService;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;


  public UserService(UserRepository userRepository, PasswordEncoder encoder, TokenService tokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = encoder;
    this.tokenService = tokenService;
  }

  @Transactional
  public List<String> register(CreateUserDto userDto){
    var errors = CheckUtils.validatePasswordAndEmail(userDto.password(), userDto.email());

    if (errors.isEmpty()) {
      var user = new User();
      user.setName(userDto.name());
      user.setEmail(userDto.email());
      user.setPassword(passwordEncoder.encode(userDto.password()));
      userRepository.save(user);
    }
    return errors;
  }

  public Boolean login(LoginDTO userLogin) {
    User user = userRepository.findByEmail(userLogin.email())
        .orElseThrow(() -> new RuntimeException("User not found."));

    if(!user.isVerified()){
      throw new RuntimeException("Email not verify");
    }

    return passwordEncoder.matches(userLogin.password(), user.getPassword());
  }

  public String generateEmailVerify(String email) {
    var token = tokenService.generateToken(email);
    return "http://localhost:8080/auth/verifyEmail/" + token;
  }

  public String verifyEmailToken(String token){
    var email = tokenService.validateToken(token);
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Link invalid."));

    var isValid = Instant.now().isBefore(user.getTimeVerify());
    if(!isValid){
      return "Time is over";
    }
    user.setVerified(true);
    userRepository.save(user);
    return "verified user";
  }

  @Scheduled(fixedRate = 50000)
  public void deleteUserNotVerify() {
    var userList = userRepository.findByVerifiedIsFalse();
   for(User user : userList){
     var isValid = Instant.now().isBefore(user.getTimeVerify());
     if(!isValid){
       userRepository.delete(user);
     }
   }
  }

  public User findById(Long id){
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found."));
  }

}