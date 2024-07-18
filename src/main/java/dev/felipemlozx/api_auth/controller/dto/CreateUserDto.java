package dev.felipemlozx.api_auth.controller.dto;

public record CreateUserDto(String name,
                            String email,
                            String password) {
}
