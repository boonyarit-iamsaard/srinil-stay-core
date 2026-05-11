package me.boonyarit.srinil.core.service;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenIssuer {

    private final RefreshTokenGenerator generator;
    private final RefreshTokenHasher hasher;

    public RefreshTokenMaterial issue() {
        String token = generator.generate();
        return new RefreshTokenMaterial(token, hasher.hash(token));
    }
}
