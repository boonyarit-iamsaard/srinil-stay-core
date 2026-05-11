package me.boonyarit.srinil.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration(proxyBeanMethods = false)
public class JwtConfig {

    @Bean
    JwtEncoder jwtEncoder(AuthSecrets authSecrets) {
        return NimbusJwtEncoder.withSecretKey(authSecrets.jwtSigningKey())
                .algorithm(MacAlgorithm.HS256)
                .build();
    }
}
