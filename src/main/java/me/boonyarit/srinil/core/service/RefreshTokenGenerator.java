package me.boonyarit.srinil.core.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import me.boonyarit.srinil.core.config.AuthProperties;

@Component
@RequiredArgsConstructor
public class RefreshTokenGenerator {

    private final AuthProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[properties.getRefreshToken().getRandomBytes()];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
