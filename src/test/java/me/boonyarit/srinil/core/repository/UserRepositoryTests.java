package me.boonyarit.srinil.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import me.boonyarit.srinil.core.TestcontainersConfiguration;
import me.boonyarit.srinil.core.entity.UserEntity;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void findsUsersByEmail() {
        UserEntity user = userRepository.save(new UserEntity("user@example.com", "password-hash"));

        assertThat(userRepository.findByEmail("user@example.com"))
                .hasValueSatisfying(foundUser -> assertThat(foundUser.getId()).isEqualTo(user.getId()));
    }

    @Test
    void checksWhetherEmailExists() {
        userRepository.save(new UserEntity("existing@example.com", "password-hash"));

        assertThat(userRepository.existsByEmail("existing@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }
}
