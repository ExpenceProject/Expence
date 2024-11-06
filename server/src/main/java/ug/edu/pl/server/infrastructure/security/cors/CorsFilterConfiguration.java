package ug.edu.pl.server.infrastructure.security.cors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(CorsFilterProperties.class)
class CorsFilterConfiguration {

    private final CorsFilterProperties properties;

    CorsFilterConfiguration(CorsFilterProperties properties) {
        this.properties = properties;
    }

    @Bean("corsFilter")
    CorsFilter corsFilter() {
        var config = buildCorsConfiguration();
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private CorsConfiguration buildCorsConfiguration() {
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);

        if (properties.maxAge() != null) {
            config.setMaxAge(properties.maxAge());
        }

        if (!CollectionUtils.isEmpty(properties.allowedMethods())) {
            config.setAllowedMethods(properties.allowedMethods());
        }

        if (!CollectionUtils.isEmpty(properties.allowedHeaders())) {
            config.setAllowedHeaders(properties.allowedHeaders());
        }

        if (!CollectionUtils.isEmpty(properties.allowedOrigins())) {
            config.setAllowedOrigins(properties.allowedOrigins());
        }

        return config;
    }
}
