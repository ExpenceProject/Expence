package ug.edu.pl.server.domain.common.storage;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public final class SampleImages {

    public static final MultipartFile IMAGE_JPG = new MockMultipartFile(
            "image",
            "image1.jpg",
            "image/jpeg",
            "fake image content 1".getBytes()
    );

    public static final MultipartFile IMAGE_PNG = new MockMultipartFile(
            "image",
            "image2.png",
            "image/png",
            "fake image content 2".getBytes()
    );

    public static final MultipartFile IMAGE_GIF = new MockMultipartFile(
            "image",
            "image3.gif",
            "image/gif",
            "fake image content 3".getBytes()
    );
}
