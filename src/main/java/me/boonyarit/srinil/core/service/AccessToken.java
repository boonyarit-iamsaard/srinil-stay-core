package me.boonyarit.srinil.core.service;

import java.time.Instant;

public record AccessToken(String value, Instant expiresAt, long expiresInSeconds) {}
