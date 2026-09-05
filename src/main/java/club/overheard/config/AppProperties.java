package club.overheard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Settings under the `app.` prefix in application.yml. */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Seed seed) {
    public record Jwt(String secret, long ttlHours) {}
    public record Seed(boolean enabled, String adminPassword) {}
}
