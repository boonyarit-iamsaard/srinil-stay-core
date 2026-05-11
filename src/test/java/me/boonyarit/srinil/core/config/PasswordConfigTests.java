package me.boonyarit.srinil.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTests {

    @Test
    void createsBcryptPasswordEncoderWithConfiguredStrength() {
        AuthProperties properties = new AuthProperties();
        properties.getPassword().setBcryptStrength(12);

        PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder(properties);
        String encoded = passwordEncoder.encode("a-password-with-enough-length");

        assertThat(encoded).startsWith("$2");
        assertThat(passwordEncoder.matches("a-password-with-enough-length", encoded))
                .isTrue();
    }
}
