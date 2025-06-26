package dev.felipemlozx.api_auth.controller.dto;

public record ResponseLoginDTO  (
    String email,
    String token
) {
}