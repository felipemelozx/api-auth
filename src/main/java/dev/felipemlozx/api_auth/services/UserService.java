package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

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
  public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = encoder;
  }
  @Transactional
  public List<String> create(CreateUserDto userDto){
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

  public List<User> listUser(){
    return userRepository.findAll();
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
