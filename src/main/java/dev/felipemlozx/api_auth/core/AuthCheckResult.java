package dev.felipemlozx.api_auth.core;

public sealed interface AuthCheckResult permits AuthCheckSuccess, AuthCheckFailure { }