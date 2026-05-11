package me.boonyarit.srinil.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import me.boonyarit.srinil.core.TestcontainersConfiguration;
import me.boonyarit.srinil.core.entity.RefreshTokenEntity;
import me.boonyarit.srinil.core.entity.UserEntity;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class RefreshTokenRepositoryTests {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    RefreshTokenRepositoryTests(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Test
    void findsRefreshTokensByHash() {
        UserEntity user = userRepository.save(new UserEntity("hash@example.com", "password-hash"));
        RefreshTokenEntity token = refreshTokenRepository.save(new RefreshTokenEntity(
                user,
                "active-token-hash",
                UUID.randomUUID(),
                Instant.parse("2026-06-10T12:00:00Z"),
                "JUnit",
                "127.0.0.1"));

        assertThat(refreshTokenRepository.findByTokenHash("active-token-hash"))
                .hasValueSatisfying(foundToken -> assertThat(foundToken.getId()).isEqualTo(token.getId()));
    }

    @Test
    void findsActiveRefreshTokensInFamily() {
        UserEntity user = userRepository.save(new UserEntity("family@example.com", "password-hash"));
        UUID familyId = UUID.randomUUID();
        Instant expiresAt = Instant.parse("2026-06-10T12:00:00Z");

        RefreshTokenEntity activeToken =
                new RefreshTokenEntity(user, "active-token-hash", familyId, expiresAt, "JUnit", "127.0.0.1");
        RefreshTokenEntity revokedToken =
                new RefreshTokenEntity(user, "revoked-token-hash", familyId, expiresAt, "JUnit", "127.0.0.1");
        revokedToken.revoke(Instant.parse("2026-05-11T12:00:00Z"), "logout");

        refreshTokenRepository.save(activeToken);
        refreshTokenRepository.save(revokedToken);

        assertThat(refreshTokenRepository.findByFamilyIdAndRevokedAtIsNull(familyId))
                .singleElement()
                .satisfies(token -> assertThat(token.getId()).isEqualTo(activeToken.getId()));
    }
}
