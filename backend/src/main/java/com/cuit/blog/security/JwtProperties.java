package com.cuit.blog.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// JWT configuration properties loaded from application properties.
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // Secret key used to sign the JWT.
    private String secret;

    // Token validity duration in milliseconds.
    private long expiration = 86_400_000L;

    // HTTP header that carries the JWT.
    private String header = "Authorization";

    // Prefix that should appear before the raw token.
    private String tokenPrefix = "Bearer";

    // Returns the token prefix normalized with a trailing space.
    public String getTokenPrefixWithSpace() {
        return tokenPrefix.endsWith(" ") ? tokenPrefix : tokenPrefix + " ";
    }
}
