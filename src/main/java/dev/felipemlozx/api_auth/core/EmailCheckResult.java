package dev.felipemlozx.api_auth.core;

public sealed interface EmailCheckResult permits EmailCheckFailure, EmailCheckSuccess {
}
