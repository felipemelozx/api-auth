package dev.felipemlozx.api_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ApiAuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiAuthApplication.class, args);
  }

  @Bean
  public BCryptPasswordEncoder getGetBcryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}