package ug.edu.pl.server.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = StorageProperties.PREFIX)
record StorageProperties(String accessKey, String secretKey, String region, String url, String bucketName) {
    static final String PREFIX = "storage";
}
