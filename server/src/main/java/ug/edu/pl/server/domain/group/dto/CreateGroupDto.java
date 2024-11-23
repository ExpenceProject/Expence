package ug.edu.pl.server.domain.group.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.infrastructure.validation.image.ValidImage;

import java.util.Set;


public record CreateGroupDto(
        @NotBlank(message = "Name must not be blank")
        String name,

        @ValidImage
        MultipartFile file,

        Set<Long> inviteesId
) {
}
