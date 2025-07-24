package dev.felipemlozx.api_auth.core;

public record LoginSuccess(String accessToken, String refreshToken) implements LoginResult { }