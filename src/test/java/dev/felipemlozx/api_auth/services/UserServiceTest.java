package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import dev.felipemlozx.api_auth.utils.CheckUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldRegisterUserWhenNoErrors() {
    CreateUserDto dto = new CreateUserDto("Felipe", "felipe@email.com", "Secure123");
    String encodedPassword = "encoded_pass";

    try (MockedStatic<CheckUtils> utils = Mockito.mockStatic(CheckUtils.class)) {
      utils.when(() -> CheckUtils.validatePasswordAndEmail(dto.password(), dto.email()))
          .thenReturn(Collections.emptyList());

      when(passwordEncoder.encode(dto.password())).thenReturn(encodedPassword);

      List<String> result = userService.register(dto);

      assertTrue(result.isEmpty());
      verify(userRepository).save(argThat(user ->
          user.getName().equals(dto.name()) &&
              user.getEmail().equals(dto.email()) &&
              user.getPassword().equals(encodedPassword)
      ));
    }
  }

  @Test
  void shouldReturnErrorsWhenValidationFails() {
    CreateUserDto dto = new CreateUserDto("Felipe", "invalid_email", "123");
    List<String> errors = List.of("Invalid email", "Weak password");

    try (MockedStatic<CheckUtils> utils = Mockito.mockStatic(CheckUtils.class)) {
      utils.when(() -> CheckUtils.validatePasswordAndEmail(dto.password(), dto.email()))
          .thenReturn(errors);

      List<String> result = userService.register(dto);

      assertEquals(errors, result);
      verify(userRepository, never()).save(any());
    }
  }

  @Test
  void shouldThrowsErrorWhenEmailIsNotVerify() {
    LoginDTO loginDTO = new LoginDTO("test@test.com", "Password!32");
    User user = new User("test", "test@test.com","Password!32", false);
    when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> userService.login(loginDTO));
    assertEquals("Email not verify", ex.getMessage());
  }

  @Test
  void shouldThrowsErrorWhenUserIsNotFound() {
    LoginDTO loginDTO = new LoginDTO("test@test.com", "Password!32");
    when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> userService.login(loginDTO));
    assertEquals("User not found.", ex.getMessage());
  }

  @Test
  void shouldReturnTrueWhenPasswordIsEquals() {
    LoginDTO loginDTO = new LoginDTO("test@test.com", "Password!32");
    User user = new User("test", "test@test.com","Password!32", true);
    when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDTO.password(), user.getPassword())).thenReturn(true);
    boolean result = userService.login(loginDTO);
    assertTrue(result);
  }

  @Test
  void shouldReturnFalseWhenPasswordIsEquals() {
    LoginDTO loginDTO = new LoginDTO("test@test.com", "Password!32");
    User user = new User("test", "test@test.com","Password32", true);
    when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDTO.password(), user.getPassword())).thenReturn(false);
    boolean result = userService.login(loginDTO);
    assertFalse(result);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFoundById() {
    long id = 1;
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> userService.findById(id));
    assertEquals("User not found.", ex.getMessage());
  }

  @Test
  void shouldReturnUserWhenUserExistsById() {
    long id = 1;
    User user = new User("test", "test@test.com","Password!32", true);
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    User result = userService.findById(id);
    assertNotNull(result);
    assertEquals(user, result);
    assertEquals(user.getEmail(), result.getEmail());
  }

  @Test
  void shouldDeleteUserNotVerify_whenTimeIsNotValid() {
    User user1 = new User("test", "test@test.com","Password32", false);
    User user2 = new User("test", "test@test.com","Password32", false);
    User user3 = new User("test", "test@test.com","Password32", false);
    user1.setTimeVerify(Instant.now().plusSeconds(500));
    mockUserTime(user2);
    mockUserTime(user3);
    when(userRepository.findByVerifiedIsFalse()).thenReturn(List.of(user1,user2,user3));

    userService.deleteUserNotVerify();
    verify(userRepository, never()).delete(user1);
    verify(userRepository).delete(user2);
    verify(userRepository).delete(user3);
  }

  private void mockUserTime(User user) {
    user.setTimeVerify(
        Instant
            .now()
            .minusSeconds(600));
  }
}
