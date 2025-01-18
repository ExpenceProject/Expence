package ug.edu.pl.server.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.regions.Region;
import ug.edu.pl.server.Profiles;

import static java.util.Objects.requireNonNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Profiles.TEST)
@AutoConfigureMockMvc
public class IntegrationTest {

    private static final String POSTGRES_IMAGE = "postgres:17.0-alpine";
    private static final String MINIO_IMAGE = "minio/minio:RELEASE.2023-09-04T19-57-37Z";
    protected static final Region REGION = Region.EU_CENTRAL_1;
    protected static final String MINIO_BUCKET = "expence";

    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
    protected static MinIOContainer minio = new MinIOContainer(DockerImageName.parse(MINIO_IMAGE));

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CacheManager cacheManager;

    @Autowired
    protected ObjectMapper objectMapper;

    static {
        postgres.start();
        minio.start();
    }

    @AfterEach
    protected void clearCache() {
        cacheManager.getCacheNames().forEach(cacheName -> requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("storage.url", minio::getS3URL);
        registry.add("storage.access-key", minio::getUserName);
        registry.add("storage.secret-key", minio::getPassword);
        registry.add("storage.region", REGION::toString);
        registry.add("storage.bucket-name", () -> MINIO_BUCKET);
    }
}
