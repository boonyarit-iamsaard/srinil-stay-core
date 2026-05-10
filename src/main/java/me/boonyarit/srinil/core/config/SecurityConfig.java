package me.boonyarit.srinil.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    private static final String[] PUBLIC_HEALTH_ENDPOINTS = {
        "/actuator/health",
        "/actuator/health/readiness",
        "/actuator/health/liveness"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, PUBLIC_HEALTH_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated())
            .httpBasic(Customizer.withDefaults())
            .build();
    }

}
