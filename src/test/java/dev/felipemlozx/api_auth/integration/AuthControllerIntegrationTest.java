package dev.felipemlozx.api_auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.felipemlozx.api_auth.config.EmbeddedRedisConfig;
import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.LoginDTO;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import dev.felipemlozx.api_auth.services.EmailService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  UserRepository userRepository;

  @Autowired
  BCryptPasswordEncoder passwordEncoder;

  @Autowired
  CacheManager cacheManager;

  @MockBean
  EmailService emailService;

  @BeforeAll
  static void startRedis() throws Exception {
    EmbeddedRedisConfig.startRedis();
  }
  @BeforeEach
  void cleanDatabase() {
    userRepository.deleteAll();
  }
  @AfterAll
  static void stopRedis() {
    EmbeddedRedisConfig.stopRedis();
  }

  @Test
  @DisplayName("Register user successfully")
  void shouldRegisterUserAndReturnSuccessResponse() throws Exception {
    CreateUserDto dto = new CreateUserDto("test", "test@gmail.com", "Password!1");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
          .characterEncoding("UTF-8")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(dto)))
      .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("User created. Verify your email."))
        .andReturn();


    User user = userRepository.findByEmail(dto.email()).orElseThrow();
    assert user.getEmail().equals(dto.email());
    assert !user.isVerified();
    assert user.getName().equals(dto.name());
    assert passwordEncoder.matches(dto.password(), user.getPassword());
    verify(emailService, times(1)).sendEmail(any(), any(), any());
  }

  @Test
  @DisplayName("Register fails with existing email")
  void shouldReturnBadRequestWhenRegisterFails() throws Exception {

    User existingUser = new User();
    existingUser.setName("test");
    existingUser.setEmail("test@gmail.com");
    existingUser.setPassword(passwordEncoder.encode("Password!1"));
    existingUser.setVerified(false);
    userRepository.save(existingUser);

    CreateUserDto dto = new CreateUserDto(existingUser.getName(), existingUser.getEmail(), existingUser.getPassword());

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data[0]").value("Email already exists"));
  }

  @Test
  @DisplayName("Login success returns tokens")
  void shouldReturnSuccessWhenLoginIsAccepted() throws Exception {

    String rawPassword = "Test#1";
    User user = new User();
    user.setName("test");
    user.setEmail("test@gmail.com");
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setVerified(true);
    userRepository.save(user);

    LoginDTO loginDto = new LoginDTO("test@gmail.com", rawPassword);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Success"))
        .andExpect(jsonPath("$.data.accessToken").exists())
        .andExpect(jsonPath("$.data.refreshToken").exists());
  }

  @Test
  @DisplayName("Login fails if email not verified")
  void shouldReturnFailsEmailNotVerifyWhenLogin() throws Exception {
    String rawPassword = "Test#1";
    User user = new User();
    user.setName("test");
    user.setEmail("test@gmail.com");
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setVerified(false);
    userRepository.save(user);

    LoginDTO loginDto = new LoginDTO("test@gmail.com", rawPassword);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Email not verified"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("Login fails with invalid credentials")
  void shouldReturnFailsInvalidCredentialsWhenLogin() throws Exception {
    String rawPassword = "Test#1";
    User user = new User();
    user.setName("test");
    user.setEmail("test@gmail.com");
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setVerified(true);
    userRepository.save(user);

    LoginDTO loginDto = new LoginDTO("test@gmail.com", "wrongPassword");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("User or password is incorrect"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("Login fails when user not registered")
  void shouldReturnFailsUserNotRegisterWhenLogin() throws Exception {
    LoginDTO loginDto = new LoginDTO("nonexistent@gmail.com", "Test#1");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("User not register."))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("Verify email token success")
  void shouldReturnSuccessWhenEmailIsVerified() throws Exception {
    String validToken = "valid-token-sample";
    String email = "test@gmail.com";
    userRepository.save(new User("test", email, "Password1@", false));
    setCache(email, validToken);

    mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify-email/" + validToken))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Email verified"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("Verify email token fails when token invalid or expired")
  void shouldReturnFailsWhenTokenIsExpired() throws Exception {
    String invalidToken = "fake-token";

    mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify-email/" + invalidToken))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Invalid or expired token"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }


  private void setCache(String email, String token) {
    Cache cache = cacheManager.getCache("EmailVerificationTokens");
    if (cache != null) cache.put(token, email);
  }
}