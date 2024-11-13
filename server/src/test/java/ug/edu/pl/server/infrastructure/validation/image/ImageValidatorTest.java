package ug.edu.pl.server.infrastructure.validation.image;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ug.edu.pl.server.domain.common.storage.SampleImages;

import static org.assertj.core.api.Assertions.assertThat;

class ImageValidatorTest {

    ImageValidator imageValidator = new ImageValidator();

    @Test
    void shouldReturnTrueForValidImage() {
        // given
        var validImage = SampleImages.IMAGE_JPG;

        // when
        var isValid = imageValidator.isValid(validImage, null);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseForInvalidImageSize() {
        // given
        var largeImage = new MockMultipartFile(
                "image",
                "largeImage.jpg",
                "image/jpeg",
                new byte[6 * 1024 * 1024] // 6MB
        );

        // when
        var isValid = imageValidator.isValid(largeImage, null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForInvalidImageType() {
        // given
        var gifImage = SampleImages.IMAGE_GIF;

        // when
        var isValid = imageValidator.isValid(gifImage, null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForNullFile() {
        // given & when
        boolean isValid = imageValidator.isValid(null, null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForEmptyFile() {
        // given
        var emptyFile = new MockMultipartFile(
                "image",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // when
        var isValid = imageValidator.isValid(emptyFile, null);

        // then
        assertThat(isValid).isFalse();
    }
}
