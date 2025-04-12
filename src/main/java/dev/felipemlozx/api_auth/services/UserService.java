package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import dev.felipemlozx.api_auth.infra.security.TokenService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  private static final String LOWERCASE_REGEX = "(?=.*[a-z])";
  private static final String UPPERCASE_REGEX = "(?=.*[A-Z])";
  private static final String DIGIT_REGEX = "(?=.*\\d)";
  private static final String SPECIAL_CHAR_REGEX = "(?=.*[@#$%^&+=!])";
  private static final String LENGTH_REGEX = ".{8,}";
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  private static final String LOWERCASE_ERROR = "Password must contain at least one lowercase letter.";
  private static final String UPPERCASE_ERROR = "Password must contain at least one uppercase letter.";
  private static final String DIGIT_ERROR = "Password must contain at least one number.";
  private static final String SPECIAL_CHAR_ERROR = "Password must contain at least one special character.";
  private static final String LENGTH_ERROR = "Password must be at least 8 characters long.";
  private static final String EMAIL_INVALID = "Email is not valid";

  public UserService(UserRepository userRepository, PasswordEncoder encoder, TokenService tokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = encoder;
    this.tokenService = tokenService;
  }

  @Transactional
  public List<String> register(CreateUserDto userDto){
    var errors = validatePasswordEmail(userDto.password(), userDto.email());

    if (errors.isEmpty()) {
      var user = new User();
      user.setName(userDto.name());
      user.setEmail(userDto.email());
      user.setPassword(passwordEncoder.encode(userDto.password()));
      userRepository.save(user);
    }
   return errors;
  }

  public String login(LoginDTO userLogin) {
    User user = userRepository.findByEmail(userLogin.email())
        .orElseThrow(() -> new RuntimeException("User not found."));

    if(!user.isVerified()){
      return "Email not verify";
    }

    if(passwordEncoder.matches(userLogin.password(), user.getPassword())){
      return this.tokenService.generateToken(user.getEmail());
    };
    return null;
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
  public static List<String> validatePasswordEmail(String password, String email) {
    List<String> errors = new ArrayList<>();

    if (!Pattern.compile(LOWERCASE_REGEX).matcher(password).find()) {
      errors.add(LOWERCASE_ERROR);
    }
    if (!Pattern.compile(UPPERCASE_REGEX).matcher(password).find()) {
      errors.add(UPPERCASE_ERROR);
    }
    if (!Pattern.compile(DIGIT_REGEX).matcher(password).find()) {
      errors.add(DIGIT_ERROR);
    }
    if (!Pattern.compile(SPECIAL_CHAR_REGEX).matcher(password).find()) {
      errors.add(SPECIAL_CHAR_ERROR);
    }
    if (!Pattern.compile(LENGTH_REGEX).matcher(password).matches()) {
      errors.add(LENGTH_ERROR);
    }
    if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
      errors.add(EMAIL_INVALID);
    }

    return errors;
  }

}