package ug.edu.pl.server.domain.group.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.domain.common.validation.image.OptionalValidImage;

public record UpdateGroupDto(@NotBlank(message = "Name must not be blank") String name,
                             @OptionalValidImage MultipartFile file) {
}
