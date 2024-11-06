package ug.edu.pl.server.domain.common.persistance;

import org.springframework.util.Assert;

/**
 * Value Object representing an image.
 * This object holds the image key used for identifying the image
 * in external storage (e.g., S3, MinIO).
 * Additional properties, such as type or size, can be added in the future.
 */
public record Image(String key) {
    public Image {
        Assert.notNull(key, "key must not be null");
    }
}
