package me.boonyarit.srinil.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import me.boonyarit.srinil.core.config.AuthProperties;
import me.boonyarit.srinil.core.config.AuthSecrets;

class JwtServiceTests {

    private static final Instant NOW = Instant.parse("2099-05-11T10:00:00Z");
    private static final String SECRET = "test-jwt-secret-with-at-least-thirty-two-bytes";

    @Test
    void issuesIdentityOnlyAccessToken() {
        AuthProperties properties = new AuthProperties();
        properties.getJwt().setSecret(SECRET);
        properties.getJwt().setAccessTokenTtl(Duration.ofMinutes(15));
        SecretKey key = new AuthSecrets(properties).jwtSigningKey();
        JwtEncoder jwtEncoder = NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build();
        JwtService jwtService = new JwtService(properties, Clock.fixed(NOW, ZoneOffset.UTC), jwtEncoder);
        UUID userId = UUID.randomUUID();

        AccessToken accessToken = jwtService.issueAccessToken(userId, new EmailIdentity("user@example.com"));

        Jwt jwt = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
                .decode(accessToken.value());
        assertThat(jwt.getSubject()).isEqualTo(userId.toString());
        assertThat(jwt.getClaimAsString("email")).isEqualTo("user@example.com");
        assertThat(jwt.getIssuedAt()).isEqualTo(NOW);
        assertThat(jwt.getExpiresAt()).isEqualTo(NOW.plus(Duration.ofMinutes(15)));
        assertThat(accessToken.expiresAt()).isEqualTo(NOW.plus(Duration.ofMinutes(15)));
        assertThat(accessToken.expiresInSeconds()).isEqualTo(900);
        assertThat(jwt.getClaims()).doesNotContainKeys("role", "roles", "permissions");
    }
}
