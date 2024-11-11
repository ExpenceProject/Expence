package ug.edu.pl.server.infrastructure.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import ug.edu.pl.server.domain.common.storage.StorageFacade;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
class StorageConfiguration {

    private final StorageProperties storageProperties;

    StorageConfiguration(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(storageProperties.accessKey(), storageProperties.secretKey())))
                .region(Region.of(storageProperties.region()))
                .endpointOverride(URI.create(storageProperties.url()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    StorageFacade storageFacade(S3Client s3Client, StorageProperties storageProperties) {
        return new StorageFacadeImpl(s3Client, storageProperties);
    }

    @Bean
    StorageInitializer storageInitializer(S3Client s3Client, StorageProperties storageProperties) {
        return new StorageInitializer(s3Client, storageProperties);
    }
}
