package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
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

  public void saveToken(String token, String email) {
    Cache cache = cacheManager.getCache("EmailVerificationTokens");
    if (cache != null) {
      cache.put(token, email);
    }
  }

  public String recuperarToken(String token) {
    Cache cache = cacheManager.getCache("EmailVerificationTokens");
    if (cache != null) {
      Cache.ValueWrapper wrapper = cache.get(token);
      if (wrapper != null) {
        return (String) wrapper.get();
      }
    }
    return null;
  }

  @Transactional
  public List<String> register(CreateUserDto userDto){
    List<String> errors = CheckUtils.validatePasswordAndEmail(userDto.password(), userDto.email());

    if (errors.isEmpty()) {
      User user = new User();
      user.setName(userDto.name());
      user.setEmail(userDto.email());
      user.setPassword(passwordEncoder.encode(userDto.password()));
      userRepository.save(user);
    }
    createEmailVerificationToken(userDto.email());
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

  public String createEmailVerificationToken(String email) {
    userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found."));
    UUID token = UUID.randomUUID();
    saveToken(token.toString(), email);
    return token.toString();
  }

  public Boolean verifyEmailToken(String token) {
    String email = recuperarToken(token);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Link invalid."));

    boolean isValid = Instant.now().isBefore(user.getTimeVerify());
    if(!isValid){
      return false;
    }
    user.setVerified(true);
    userRepository.save(user);
    return true;
  }

  // 30 minutos em milissegundos
  @Scheduled(fixedRate = 1800000)
  public void deleteUserNotVerify() {
    List<User> userList = userRepository.findByVerifiedIsFalse();
    for(User user : userList){
      boolean isValid = Instant.now().isBefore(user.getTimeVerify());
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