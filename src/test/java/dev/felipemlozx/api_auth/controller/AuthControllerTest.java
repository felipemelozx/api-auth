package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.controller.dto.ResponseLoginDTO;
import dev.felipemlozx.api_auth.core.AuthError;
import dev.felipemlozx.api_auth.core.LoginFailure;
import dev.felipemlozx.api_auth.core.LoginResult;
import dev.felipemlozx.api_auth.core.LoginSuccess;
import dev.felipemlozx.api_auth.services.AuthService;
import dev.felipemlozx.api_auth.utils.ApiResponse;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AuthControllerTest {

  @InjectMocks
  private AuthController authController;

  @Mock
  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldRegisterUserAndReturnSuccessResponse() throws MessagingException {
    List<String> userList = new ArrayList<>();
    CreateUserDto body = new CreateUserDto("test", "test@gmail.com", "Password!1");
    when(authService.register(body)).thenReturn(userList);

    ResponseEntity<ApiResponse<List<String>>> result = authController.register(body);
    assertNotNull(result);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertTrue(result.getBody().isSuccess());
    assertEquals("User created. Verify your email.", result.getBody().getMessage());
  }

  @Test
  void shouldReturnBadRequestWhenRegisterFails() throws MessagingException {
    List<String> fails = List.of("Email already exists");
    CreateUserDto body = new CreateUserDto("test", "test@gmail.com", "Password!1");
    when(authService.register(body)).thenReturn(fails);

    ResponseEntity<ApiResponse<List<String>>> result = authController.register(body);
    assertNotNull(result);
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    assertFalse(result.getBody().isSuccess());
    assertEquals(fails, result.getBody().getData());
  }

  @Test
  void shouldReturnSussedWhenLoginIsAccepted() {
    LoginDTO request = new LoginDTO("test@gmail.com", "Test#1");
    LoginResult responseAuth = new LoginSuccess("accessToken", "refreshToken");
    ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO("accessToken", "refreshToken");
    when(authService.login(request)).thenReturn(responseAuth);

    ResponseEntity<ApiResponse<ResponseLoginDTO>> response = authController.login(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseLoginDTO, response.getBody().getData());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Success", response.getBody().getMessage());
  }

  @Test
  void shouldReturnFailsEmailNotVerifyWhenLoginIsAccepted() {
    LoginDTO request = new LoginDTO("test@gmail.com", "Test#1");
    LoginResult responseAuth = new LoginFailure(AuthError.EMAIL_NOT_VERIFIED);
    when(authService.login(request)).thenReturn(responseAuth);

    ResponseEntity<ApiResponse<ResponseLoginDTO>> response = authController.login(request);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Email not verified", response.getBody().getMessage());
    assertNull(response.getBody().getData());
  }

  @Test
  void shouldReturnFailsInvalidCredentialsWhenLoginIsAccepted() {
    LoginDTO request = new LoginDTO("test@gmail.com", "Test#1");
    LoginResult responseAuth = new LoginFailure(AuthError.INVALID_CREDENTIALS);
    when(authService.login(request)).thenReturn(responseAuth);

    ResponseEntity<ApiResponse<ResponseLoginDTO>> response = authController.login(request);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("User or password is incorrect", response.getBody().getMessage());
    assertNull(response.getBody().getData());
  }

  @Test
  void shouldReturnFailsUserNotRegisterWhenLoginIsAccepted() {
    LoginDTO request = new LoginDTO("test@gmail.com", "Test#1");
    LoginResult responseAuth = new LoginFailure(AuthError.USER_NOT_REGISTER);
    when(authService.login(request)).thenReturn(responseAuth);

    ResponseEntity<ApiResponse<ResponseLoginDTO>> response = authController.login(request);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("User not register.", response.getBody().getMessage());
    assertNull(response.getBody().getData());
  }

  @Test
  void shouldReturnSussedWhenEmailIsVerified() {
    String token = "fake-token";
    boolean responseAuth = true;

    when(authService.verifyEmailToken(token)).thenReturn(responseAuth);

    ResponseEntity<ApiResponse<Void>> response = authController.verifyEmail(token);
    assertEquals("Email verified", response.getBody().getMessage());
    assertNull(response.getBody().getData());
  }

  @Test
  void shouldReturnFailsWhenTokenIsExpired() {
    String token = "fake-token";
    boolean responseAuth = false;

    when(authService.verifyEmailToken(token)).thenReturn(responseAuth);

    ResponseEntity<ApiResponse<Void>> response = authController.verifyEmail(token);
    assertEquals("Invalid or expired token", response.getBody().getMessage());
    assertNull(response.getBody().getData());
  }
}
