package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.infra.security.TokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

  @InjectMocks
  private AuthService authService;

  @Mock
  private UserService userService;

  @Mock
  private EmailService emailService;

  @Mock
  private TokenService tokenService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldRegisterUserAndSendVerificationEmail_whenDataIsValid() throws MessagingException {
    CreateUserDto createUserDto = new CreateUserDto("name", "test@gmail.com", "Password123!");
    String token = UUID.randomUUID().toString();

    when(userService.register(createUserDto)).thenReturn(List.of());
    when(userService.createEmailVerificationToken(createUserDto.email())).thenReturn(token);
    doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

    List<String> result = authService.register(createUserDto);

    verify(emailService).sendEmail(anyString(), anyString(), anyString());
    verify(userService).createEmailVerificationToken(createUserDto.email());
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldGenerateVerificationLinkWithCorrectToken() {
    String token = UUID.randomUUID().toString();
    String result = authService.generateLinkToVerifyEmail(token);
    assertTrue(result.contains(token));
  }

  @Test
  void shouldReturnToken_whenLoginIsSuccessful() {
    LoginDTO loginDTO = new LoginDTO("test@gmail.com", "Password123!");
    String token = UUID.randomUUID().toString();
    ResponseLoginDTO mock = new ResponseLoginDTO(token, token);

    when(userService.login(loginDTO)).thenReturn(true);
    when(tokenService.generateToken(loginDTO.email())).thenReturn(token);

    ResponseLoginDTO result = authService.login(loginDTO);
    verify(userService).login(loginDTO);
    assertEquals(mock, result);
  }

  @Test
  void shouldThrowException_whenLoginCredentialsAreInvalid() {
    LoginDTO loginDTO = new LoginDTO("test@gmail.com", "Password123!");

    when(userService.login(loginDTO)).thenReturn(false);

    assertNull(authService.login(loginDTO));
    verify(userService).login(loginDTO);
  }

  @Test
  void shouldReturnTrue_whenVerifyEmailTokenIsValid() {
    String token = "token";
    when(userService.verifyEmailToken(token)).thenReturn(true);
    assertTrue(authService.verifyEmailToken(token));
  }

  @Test
  void shouldReturnFalse_whenVerifyEmailTokenIsInvalid() {
    String token = "token";
    when(userService.verifyEmailToken(token)).thenReturn(false);
    assertFalse(authService.verifyEmailToken(token));
  }

  @Test
  void shouldNotSendVerificationEmail_whenRegisterReturnsNonEmptyList() throws MessagingException {
    CreateUserDto createUserDto = new CreateUserDto("name", "test@gmail.com", "Password123!");
    List<String> errors = List.of("Password is invalid");

    when(userService.register(createUserDto)).thenReturn(errors);

    List<String> result = authService.register(createUserDto);

    verify(userService).register(createUserDto);
    verify(userService, org.mockito.Mockito.never()).createEmailVerificationToken(anyString());
    verify(emailService, org.mockito.Mockito.never()).sendEmail(anyString(), anyString(), anyString());
    assertEquals(errors, result);
  }
}
