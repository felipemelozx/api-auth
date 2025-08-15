package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.core.AuthCheckFailure;
import dev.felipemlozx.api_auth.core.AuthCheckSuccess;
import dev.felipemlozx.api_auth.core.AuthError;
import dev.felipemlozx.api_auth.core.LoginFailure;
import dev.felipemlozx.api_auth.core.LoginResult;
import dev.felipemlozx.api_auth.core.LoginSuccess;
import dev.felipemlozx.api_auth.dto.CreateUserDTO;
import dev.felipemlozx.api_auth.dto.LoginDTO;
import dev.felipemlozx.api_auth.dto.UserJwtDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.infra.security.TokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

  @InjectMocks
  @Spy
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
    CreateUserDTO CreateUserDTO = new CreateUserDTO("name", "test@gmail.com", "Password123!");
    String token = UUID.randomUUID().toString();

    when(userService.register(CreateUserDTO)).thenReturn(List.of());
    when(userService.createEmailVerificationToken(CreateUserDTO.email())).thenReturn(token);
    doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

    List<String> result = authService.register(CreateUserDTO);

    verify(emailService).sendEmail(anyString(), anyString(), anyString());
    verify(userService).createEmailVerificationToken(CreateUserDTO.email());
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
    User user = new User("test", "test@gmail.com", "Password123!", true);
    String token = UUID.randomUUID().toString();

    when(userService.login(loginDTO)).thenReturn(new AuthCheckSuccess(user));
    when(tokenService.generateToken(any())).thenReturn(token);

    LoginResult result = authService.login(loginDTO);

    verify(userService).login(loginDTO);

    LoginSuccess expected = new LoginSuccess(token, token);
    assertTrue(expected.equals(result));
  }

  @Test
  void shouldReturnNull_whenLoginCredentialsAreInvalid() {
    LoginDTO loginDTO = new LoginDTO("test@gmail.com", "Password123!");
    AuthCheckFailure error = new AuthCheckFailure(AuthError.INVALID_CREDENTIALS);
    when(userService.login(loginDTO)).thenReturn(error);

    LoginFailure response = (LoginFailure) authService.login(loginDTO);

    assertTrue(response.error().equals(error.error()));
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
    CreateUserDTO CreateUserDTO = new CreateUserDTO("name", "test@gmail.com", "Password123!");
    List<String> errors = List.of("Password is invalid");

    when(userService.register(CreateUserDTO)).thenReturn(errors);

    List<String> result = authService.register(CreateUserDTO);

    verify(userService).register(CreateUserDTO);
    verify(userService, never()).createEmailVerificationToken(anyString());
    verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    assertEquals(errors, result);
  }

  @Test
  void shouldNotSedEmailWhenTokenIsInValid() throws MessagingException {
    CreateUserDTO CreateUserDTO = new CreateUserDTO("name", "test@gmail.com", "Password123!");

    when(userService.register(CreateUserDTO)).thenReturn(List.of());
    when(userService.createEmailVerificationToken(CreateUserDTO.email())).thenReturn(null);

    List<String> result = authService.register(CreateUserDTO);

    verify(userService).register(CreateUserDTO);
    verify(userService).createEmailVerificationToken(CreateUserDTO.email());
    verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
  }
}
