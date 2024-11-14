package ug.edu.pl.server.infrastructure.storage;

import io.hypersistence.tsid.TSID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.common.storage.exception.DeletingImageException;
import ug.edu.pl.server.domain.common.storage.exception.UploadingImageException;

@Log
@Slf4j
@EnableConfigurationProperties(StorageProperties.class)
class StorageFacadeImpl implements StorageFacade {

    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    StorageFacadeImpl(S3Client s3Client, StorageProperties storageProperties) {
        this.s3Client = s3Client;
        this.storageProperties = storageProperties;
    }

    @Override
    public String upload(MultipartFile file) {
        var uniqueKey = generateUniqueKey(file.getOriginalFilename());

        try {
            var request = PutObjectRequest.builder()
                    .bucket(storageProperties.bucketName())
                    .key(uniqueKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return uniqueKey;
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new UploadingImageException();
        }
    }

    @Override
    public void delete(String key) {
        try {
            var request = DeleteObjectRequest.builder()
                    .bucket(storageProperties.bucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            log.error("Error deleting file with key {}: {}", key, e.getMessage(), e);
            throw new DeletingImageException();
        }
    }

    private String generateUniqueKey(String fileName) {
        return TSID.Factory.getTsid() + "_" + fileName;
    }
}
