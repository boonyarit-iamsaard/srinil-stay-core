package me.boonyarit.srinil.core.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    @Valid
    private final Jwt jwt = new Jwt();

    @Valid
    private final RefreshToken refreshToken = new RefreshToken();

    @Valid
    private final Password password = new Password();

    @Valid
    private final Cors cors = new Cors();

    public Jwt getJwt() {
        return jwt;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public Password getPassword() {
        return password;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Jwt {

        private String secret = "";

        private Duration accessTokenTtl = Duration.ofMinutes(15);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getAccessTokenTtl() {
            return accessTokenTtl;
        }

        public void setAccessTokenTtl(Duration accessTokenTtl) {
            this.accessTokenTtl = accessTokenTtl;
        }
    }

    public static class RefreshToken {

        private String hashSecret = "";

        private Duration ttl = Duration.ofDays(30);

        @NotBlank
        private String cookieName;

        @NotBlank
        private String cookiePath;

        private boolean cookieSecure = true;

        @NotBlank
        private String cookieSameSite;

        @Min(32)
        private int randomBytes = 32;

        public String getHashSecret() {
            return hashSecret;
        }

        public void setHashSecret(String hashSecret) {
            this.hashSecret = hashSecret;
        }

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public String getCookiePath() {
            return cookiePath;
        }

        public void setCookiePath(String cookiePath) {
            this.cookiePath = cookiePath;
        }

        public boolean isCookieSecure() {
            return cookieSecure;
        }

        public void setCookieSecure(boolean cookieSecure) {
            this.cookieSecure = cookieSecure;
        }

        public String getCookieSameSite() {
            return cookieSameSite;
        }

        public void setCookieSameSite(String cookieSameSite) {
            this.cookieSameSite = cookieSameSite;
        }

        public int getRandomBytes() {
            return randomBytes;
        }

        public void setRandomBytes(int randomBytes) {
            this.randomBytes = randomBytes;
        }
    }

    public static class Password {

        @Min(4)
        @Max(31)
        private int bcryptStrength = 12;

        public int getBcryptStrength() {
            return bcryptStrength;
        }

        public void setBcryptStrength(int bcryptStrength) {
            this.bcryptStrength = bcryptStrength;
        }
    }

    public static class Cors {

        private List<String> allowedOrigins = new ArrayList<>();

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
