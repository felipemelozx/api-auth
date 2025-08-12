package dev.felipemlozx.api_auth.dto;

public record CreateUserDto(String name,
                            String email,
                            String password) {
}
