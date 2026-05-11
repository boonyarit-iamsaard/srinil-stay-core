package me.boonyarit.srinil.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import me.boonyarit.srinil.core.config.AuthProperties;

class RefreshTokenGeneratorTests {

    @Test
    void generatesOpaqueBase64UrlTokenWithoutPadding() {
        RefreshTokenGenerator generator = new RefreshTokenGenerator(new AuthProperties());

        String token = generator.generate();

        assertThat(token).hasSizeGreaterThanOrEqualTo(43).doesNotContain("=").matches("[A-Za-z0-9_-]+");
    }

    @Test
    void generatesDifferentTokens() {
        RefreshTokenGenerator generator = new RefreshTokenGenerator(new AuthProperties());

        assertThat(generator.generate()).isNotEqualTo(generator.generate());
    }
}
