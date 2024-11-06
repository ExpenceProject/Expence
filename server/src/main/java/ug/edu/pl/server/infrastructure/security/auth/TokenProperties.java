package ug.edu.pl.server.infrastructure.security.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = TokenProperties.PREFIX)
record TokenProperties(String secret, long expirationMs, String issuer) {
    static final String PREFIX = "web.security.jwt";
}
