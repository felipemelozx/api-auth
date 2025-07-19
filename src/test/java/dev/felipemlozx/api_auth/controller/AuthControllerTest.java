package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
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
    assertEquals("User registered.", result.getBody().getMessage());
    assertEquals("Verification email sent.", result.getBody().getData().get(0));
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

}
