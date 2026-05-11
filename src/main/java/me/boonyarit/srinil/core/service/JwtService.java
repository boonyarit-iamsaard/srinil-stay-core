package me.boonyarit.srinil.core.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import me.boonyarit.srinil.core.config.AuthProperties;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AuthProperties properties;
    private final Clock clock;
    private final JwtEncoder jwtEncoder;

    public AccessToken issueAccessToken(UUID userId, EmailIdentity emailIdentity) {
        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plus(properties.getJwt().getAccessTokenTtl());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.toString())
                .claim("email", emailIdentity.value())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        String token =
                jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
        return new AccessToken(
                token, expiresAt, properties.getJwt().getAccessTokenTtl().toSeconds());
    }
}
