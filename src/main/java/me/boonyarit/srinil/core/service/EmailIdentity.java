package me.boonyarit.srinil.core.service;

import java.util.Locale;
import java.util.Objects;

public record EmailIdentity(String value) {

    public EmailIdentity {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static EmailIdentity normalize(String email) {
        return new EmailIdentity(
                Objects.requireNonNull(email, "email must not be null").trim().toLowerCase(Locale.ROOT));
    }
}
