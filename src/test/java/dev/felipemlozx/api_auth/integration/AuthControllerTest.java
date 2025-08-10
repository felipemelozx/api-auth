package dev.felipemlozx.api_auth.integration;

import dev.felipemlozx.api_auth.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @BeforeAll
  static void up() throws IOException {
    EmbeddedRedisConfig.startRedis();
  }

  @AfterAll
  static void down() {
    EmbeddedRedisConfig.stopRedis();
  }


  @Test
  @DisplayName("Verify that the verify-email endpoint returns BadRequest for an invalid token")
  void shouldReturnBadRequestForInvalidToken() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/auth/verify-email/fake-token"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andDo(MockMvcResultHandlers.print());
  }
}
