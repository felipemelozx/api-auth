package dev.felipemlozx.api_auth.dto;

public record CreateUserDTO(String name,
                            String email,
                            String password) {
}
