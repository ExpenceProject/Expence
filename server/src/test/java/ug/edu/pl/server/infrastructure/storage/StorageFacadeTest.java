package ug.edu.pl.server.infrastructure.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.common.storage.SampleImages;
import ug.edu.pl.server.domain.common.storage.StorageFacade;

import static org.assertj.core.api.Assertions.assertThat;

class StorageFacadeTest extends IntegrationTest {

    @Autowired
    private StorageFacade storageFacade;

    @Autowired
    private S3Client s3Client;

    @Test
    void shouldUploadAndDeleteFile() {
        // given
        var file = SampleImages.IMAGE_PNG;

        // when
        var key = storageFacade.upload(file);

        // then
        var response = getObjectsFromBucket(key);
        assertThat(response.contents()).isNotEmpty();
        assertThat(response.contents().getFirst().key()).isEqualTo(key);

        // when
        storageFacade.delete(key);

        // then
        var responseFromDelete = getObjectsFromBucket(key);
        assertThat(responseFromDelete.contents()).isEmpty();
    }

    private ListObjectsV2Response getObjectsFromBucket(String key) {
        var request = ListObjectsV2Request.builder()
                .bucket(MINIO_BUCKET)
                .prefix(key)
                .build();

        return s3Client.listObjectsV2(request);
    }
}
