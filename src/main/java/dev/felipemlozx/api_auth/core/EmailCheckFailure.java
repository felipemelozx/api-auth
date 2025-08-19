package dev.felipemlozx.api_auth.core;

public record EmailCheckFailure(Email error) implements EmailCheckResult { }