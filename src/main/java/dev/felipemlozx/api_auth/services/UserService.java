package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.core.AuthCheckFailure;
import dev.felipemlozx.api_auth.core.AuthCheckResult;
import dev.felipemlozx.api_auth.core.AuthCheckSuccess;
import dev.felipemlozx.api_auth.core.AuthError;
import dev.felipemlozx.api_auth.dto.CreateUserDTO;
import dev.felipemlozx.api_auth.dto.LoginDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import dev.felipemlozx.api_auth.utils.CheckUtils;
import jakarta.transaction.Transactional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CacheManager cacheManager;

  public UserService(UserRepository userRepository, PasswordEncoder encoder,  CacheManager cacheManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = encoder;
    this.cacheManager = cacheManager;
  }

  @Transactional
  public List<String> register(CreateUserDTO userDto) {
    List<String> errors = CheckUtils.validatePasswordAndEmail(userDto.password(), userDto.email());

    boolean userExist = userRepository.existsByEmail(userDto.email());
    if(userExist) errors.add("Email already exists");

    if (errors.isEmpty()) {
      User user = new User();
      user.setName(userDto.name());
      user.setEmail(userDto.email());
      user.setPassword(passwordEncoder.encode(userDto.password()));
      userRepository.save(user);
    }
    return errors;
  }

  public AuthCheckResult login(LoginDTO userLogin) {
    Optional<User> maybeUser = userRepository.findByEmail(userLogin.email());

    if(maybeUser.isEmpty()) return new AuthCheckFailure(AuthError.USER_NOT_REGISTER);
    User user = maybeUser.get();

    if(!user.isVerified()) return new AuthCheckFailure(AuthError.EMAIL_NOT_VERIFIED);

    boolean matches = passwordEncoder.matches(userLogin.password(), user.getPassword());

    if (!matches) {
      return new AuthCheckFailure(AuthError.INVALID_CREDENTIALS);
    }

    return new AuthCheckSuccess(user);
  }

  public String createEmailVerificationToken(String email) {
    Optional<User> maybeUser = userRepository.findByEmail(email);
    if(maybeUser.isEmpty()) return null;

    UUID token = UUID.randomUUID();
    saveToken(token.toString(), email);
    return token.toString();
  }

  public Boolean verifyEmailToken(String token) {
    String email = recoverToken(token);
    if(email == null) return false;

    Optional<User> maybeUser = userRepository.findByEmail(email);
    if(maybeUser.isEmpty()) return false;
    User user = maybeUser.get();

    boolean isValid = Instant.now().isBefore(user.getTimeVerify());
    if(!isValid){
      return false;
    }

    user.setVerified(true);
    userRepository.save(user);
    return true;
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  // 30 minutos em milissegundos
  @Scheduled(fixedRate = 1800000)
  public void deleteUserNotVerify() {
    List<User> userList = userRepository.findByVerifiedIsFalse();
    Instant now = Instant.now();
    for(User user : userList){
      boolean isValid = now.isBefore(user.getTimeVerify());
      if(!isValid) userRepository.delete(user);
    }
  }

  public User findById(Long id){
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found."));
  }

  public void saveToken(String token, String email) {
    Cache cache = cacheManager.getCache("EmailVerificationTokens");
    if (cache != null) cache.put(token, email);
  }

  public String recoverToken(String token) {
    Cache cache = cacheManager.getCache("EmailVerificationTokens");
    if (cache == null) return null;
    Cache.ValueWrapper wrapper = cache.get(token);
    return wrapper != null ? (String) wrapper.get() : null;
  }
}