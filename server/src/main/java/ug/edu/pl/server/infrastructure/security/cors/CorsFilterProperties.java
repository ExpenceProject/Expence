package ug.edu.pl.server.infrastructure.security.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = CorsFilterProperties.PREFIX)
record CorsFilterProperties(
        List<String> urlPatterns,
        List<String> allowedOrigins,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        Long maxAge,
        int order
) {
    static final String PREFIX = "web.filter.cors";

    CorsFilterProperties {
        if (order == 0) {
            order = 100;
        }
    }
}
