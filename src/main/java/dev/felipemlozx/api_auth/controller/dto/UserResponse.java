package dev.felipemlozx.api_auth.controller.dto;

import dev.felipemlozx.api_auth.entity.User;

public record UserResponse(Long id,
                           String name,
                           String email) {

  public static UserResponse fromUser(User user){
    return new UserResponse(
        user.getId(),
        user.getName(),
        user.getEmail()
    );
  }
}
