package dev.felipemlozx.api_auth.core;

import dev.felipemlozx.api_auth.entity.User;

public record AuthCheckSuccess(User user) implements AuthCheckResult { }