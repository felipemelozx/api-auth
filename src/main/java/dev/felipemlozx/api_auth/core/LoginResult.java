package dev.felipemlozx.api_auth.core;

public sealed interface LoginResult permits LoginSuccess, LoginFailure { }