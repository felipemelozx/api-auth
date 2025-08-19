package dev.felipemlozx.api_auth.core;

public enum AuthError {
    EMAIL_NOT_VERIFIED,
    INVALID_CREDENTIALS,
    REFRESH_TOKEN_INVALID,
    USER_NOT_REGISTER,
    TIME_TO_CHECK_EMAIL_IS_OVER,
    USER_IS_VERIFIED
}