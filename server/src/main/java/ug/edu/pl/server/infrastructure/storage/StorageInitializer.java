package ug.edu.pl.server.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

@Slf4j
class StorageInitializer implements CommandLineRunner {

    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    StorageInitializer(S3Client s3Client, StorageProperties storageProperties) {
        this.s3Client = s3Client;
        this.storageProperties = storageProperties;
    }

    @Override
    public void run(String... args) {
        var bucketName = storageProperties.bucketName();

        try {
            var createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
            log.info("Bucket {} created successfully.", bucketName);

            updateBucketToPublicReadOnlyPolicy(bucketName);
        } catch (BucketAlreadyExistsException e) {
            log.info("Bucket {} already exists.", bucketName);
        } catch (BucketAlreadyOwnedByYouException e) {
            log.info("Bucket {} already owned by you.", bucketName);
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage());
        }
    }

    private void updateBucketToPublicReadOnlyPolicy(String bucketName) {
        try {
            String publicReadOnlyPolicy = String.format(
                    "{" +
                            "\"Version\": \"2012-10-17\"," +
                            "\"Statement\": [{" +
                            "\"Effect\": \"Allow\"," +
                            "\"Principal\": \"*\"," +
                            "\"Action\": \"s3:GetObject\"," +
                            "\"Resource\": \"arn:aws:s3:::%s/*\"" +
                            "}]" +
                            "}", bucketName);

            var putBucketPolicyRequest = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(publicReadOnlyPolicy)
                    .build();

            s3Client.putBucketPolicy(putBucketPolicyRequest);
            log.info("Public read-only policy applied to bucket {}.", bucketName);
        } catch (Exception e) {
            log.error("Error updating read-only policy: {}", e.getMessage());
        }
    }
}
