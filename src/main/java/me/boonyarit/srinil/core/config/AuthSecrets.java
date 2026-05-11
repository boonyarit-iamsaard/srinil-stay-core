package me.boonyarit.srinil.core.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthSecrets {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final int MIN_SECRET_BYTES = 32;

    private final AuthProperties properties;

    public AuthSecrets(AuthProperties properties) {
        this.properties = properties;
    }

    public SecretKey jwtSigningKey() {
        return new SecretKeySpec(secretBytes(properties.getJwt().getSecret(), "JWT secret"), HMAC_SHA_256);
    }

    public byte[] refreshTokenHashSecret() {
        return secretBytes(properties.getRefreshToken().getHashSecret(), "Refresh token hash secret");
    }

    private static byte[] secretBytes(String secret, String label) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException(label + " must be configured");
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(label + " must be at least 32 bytes");
        }
        return bytes;
    }
}
