package dev.felipemlozx.api_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiAuthApplication {
  public static void main(String[] args) {
    SpringApplication.run(ApiAuthApplication.class, args);
  }
}