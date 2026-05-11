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
import lombok.Getter;
import lombok.Setter;

@Getter
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

    @Getter
    @Setter
    public static class Jwt {

        private String secret = "";

        private Duration accessTokenTtl = Duration.ofMinutes(15);
    }

    @Getter
    @Setter
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
    }

    @Getter
    @Setter
    public static class Password {

        @Min(4)
        @Max(31)
        private int bcryptStrength = 12;
    }

    @Getter
    @Setter
    public static class Cors {

        private List<String> allowedOrigins = new ArrayList<>();
    }
}
