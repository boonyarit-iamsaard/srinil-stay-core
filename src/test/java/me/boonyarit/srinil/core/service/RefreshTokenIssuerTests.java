package me.boonyarit.srinil.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import me.boonyarit.srinil.core.config.AuthProperties;
import me.boonyarit.srinil.core.config.AuthSecrets;

class RefreshTokenIssuerTests {

    @Test
    void issuesRefreshTokenMaterial() {
        AuthProperties properties = new AuthProperties();
        properties.getRefreshToken().setHashSecret("hash-secret-with-at-least-thirty-two-bytes");
        RefreshTokenGenerator generator = new RefreshTokenGenerator(properties);
        RefreshTokenHasher hasher = new RefreshTokenHasher(new AuthSecrets(properties));
        RefreshTokenIssuer issuer = new RefreshTokenIssuer(generator, hasher);

        RefreshTokenMaterial material = issuer.issue();

        assertThat(material.value()).isNotBlank();
        assertThat(material.hash()).isEqualTo(hasher.hash(material.value()));
    }
}
