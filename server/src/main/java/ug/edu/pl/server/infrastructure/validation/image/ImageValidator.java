package ug.edu.pl.server.infrastructure.validation.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final long MAX_SIZE = 5 * 1024 * 1024;  // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/jpg"};

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return false;
        }

        if (multipartFile.getSize() > MAX_SIZE) {
            return false;
        }

        var contentType = multipartFile.getContentType();
        if (contentType == null) {
            return false;
        }

        for (var allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }

        return false;
    }
}
