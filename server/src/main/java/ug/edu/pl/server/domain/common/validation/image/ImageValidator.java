package ug.edu.pl.server.domain.common.validation.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

class ImageValidationBase {
  private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
  private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/jpg"};

  public static boolean validateImage(MultipartFile multipartFile) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      return false; // Default behavior for mandatory image validation
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

class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
  @Override
  public boolean isValid(
      MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
    return ImageValidationBase.validateImage(multipartFile);
  }
}

class OptionalImageValidator implements ConstraintValidator<OptionalValidImage, MultipartFile> {

  @Override
  public boolean isValid(
      MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
    // Allow empty file
    if (multipartFile == null || multipartFile.isEmpty()) {
      return true;
    }

    return ImageValidationBase.validateImage(multipartFile);
  }
}
