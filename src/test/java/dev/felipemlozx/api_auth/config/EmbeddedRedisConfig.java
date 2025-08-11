package dev.felipemlozx.api_auth.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.util.TestSocketUtils;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class EmbeddedRedisConfig {
  private static RedisServer redisServer;

  @PostConstruct
  public static void startRedis() throws IOException {
    int port = 6379;
    redisServer = new RedisServer(port);
    redisServer.start();
    System.out.printf("Redis server up! port: " + port);
  }

  @PreDestroy
  public static void stopRedis(){
    if(redisServer != null){
      redisServer.stop();
      System.out.println("Redis server stoped!");
    }
  }
}
