package me.boonyarit.srinil.core.service;

import org.springframework.stereotype.Component;

@Component
public class RefreshTokenIssuer {

    private final RefreshTokenGenerator generator;
    private final RefreshTokenHasher hasher;

    public RefreshTokenIssuer(RefreshTokenGenerator generator, RefreshTokenHasher hasher) {
        this.generator = generator;
        this.hasher = hasher;
    }

    public RefreshTokenMaterial issue() {
        String token = generator.generate();
        return new RefreshTokenMaterial(token, hasher.hash(token));
    }
}
