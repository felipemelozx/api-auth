package dev.felipemlozx.api_auth.controller.dto;

public record ResponseLoginDTO  (
    String accessToken,
    String refreshToken
) {
}