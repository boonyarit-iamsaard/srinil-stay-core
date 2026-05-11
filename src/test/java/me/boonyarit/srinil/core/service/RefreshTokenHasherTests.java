package me.boonyarit.srinil.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import me.boonyarit.srinil.core.config.AuthProperties;
import me.boonyarit.srinil.core.config.AuthSecrets;

class RefreshTokenHasherTests {

    @Test
    void hashesRefreshTokenWithConfiguredSecret() {
        RefreshTokenHasher hasher = newHasher("hash-secret-one-with-at-least-thirty-two-bytes");

        String hash = hasher.hash("refresh-token");

        assertThat(hash).hasSize(64);
        assertThat(hash).matches("[0-9a-f]+");
        assertThat(hash).isEqualTo(hasher.hash("refresh-token"));
    }

    @Test
    void differentSecretsProduceDifferentHashes() {
        RefreshTokenHasher firstHasher = newHasher("hash-secret-one-with-at-least-thirty-two-bytes");
        RefreshTokenHasher secondHasher = newHasher("hash-secret-two-with-at-least-thirty-two-bytes");

        assertThat(firstHasher.hash("refresh-token")).isNotEqualTo(secondHasher.hash("refresh-token"));
    }

    private static AuthProperties propertiesWithHashSecret(String secret) {
        AuthProperties properties = new AuthProperties();
        properties.getRefreshToken().setHashSecret(secret);
        return properties;
    }

    private static RefreshTokenHasher newHasher(String secret) {
        return new RefreshTokenHasher(new AuthSecrets(propertiesWithHashSecret(secret)));
    }
}
