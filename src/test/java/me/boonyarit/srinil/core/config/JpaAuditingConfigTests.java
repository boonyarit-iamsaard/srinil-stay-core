package me.boonyarit.srinil.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import me.boonyarit.srinil.core.TestcontainersConfiguration;
import me.boonyarit.srinil.core.entity.UserEntity;

@Import({TestcontainersConfiguration.class, JpaAuditingConfigTests.FixedClockConfig.class})
@SpringBootTest
@Transactional
class JpaAuditingConfigTests {

    private static final Instant NOW = Instant.parse("2026-05-10T12:00:00Z");

    private final EntityManager entityManager;

    @Autowired
    JpaAuditingConfigTests(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    void populatesAuditTimestampsFromConfiguredClock() {
        UserEntity user = new UserEntity("audit@example.com", "password-hash");

        entityManager.persist(user);
        entityManager.flush();

        assertThat(user.getCreatedAt()).isEqualTo(NOW);
        assertThat(user.getUpdatedAt()).isEqualTo(NOW);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClockConfig {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW, ZoneOffset.UTC);
        }
    }
}
