package dev.felipemlozx.api_auth.dto;

public record ResponseLoginDTO  (
    String accessToken,
    String refreshToken
) {
}