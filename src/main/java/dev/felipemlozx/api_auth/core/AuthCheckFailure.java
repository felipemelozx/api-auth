package dev.felipemlozx.api_auth.core;

public record AuthCheckFailure(AuthError error) implements AuthCheckResult { }