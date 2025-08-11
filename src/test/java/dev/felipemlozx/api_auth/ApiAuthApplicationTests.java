package dev.felipemlozx.api_auth;

import dev.felipemlozx.api_auth.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.io.IOException;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ApiAuthApplicationTests {

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
	 void contextLoads() {
	 }

}
