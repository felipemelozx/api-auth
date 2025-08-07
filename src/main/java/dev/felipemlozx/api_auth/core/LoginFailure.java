package dev.felipemlozx.api_auth.core;

public record LoginFailure(AuthError error) implements LoginResult { }