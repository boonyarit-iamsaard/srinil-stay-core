package me.boonyarit.srinil.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class EmailIdentityTests {

    @Test
    void normalizesEmail() {
        assertThat(EmailIdentity.normalize("  User.Name+Test@Example.COM  ").value())
                .isEqualTo("user.name+test@example.com");
    }

    @Test
    void rejectsNullEmailDuringNormalization() {
        assertThatNullPointerException()
                .isThrownBy(() -> EmailIdentity.normalize(null))
                .withMessage("email must not be null");
    }

    @Test
    void rejectsNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> new EmailIdentity(null))
                .withMessage("value must not be null");
    }
}
