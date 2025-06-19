package dev.felipemlozx.api_auth.service;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.repository.UserRepository;
import dev.felipemlozx.api_auth.services.UserService;
import dev.felipemlozx.api_auth.utils.CheckUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
