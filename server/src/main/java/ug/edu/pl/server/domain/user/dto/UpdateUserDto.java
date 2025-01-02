package ug.edu.pl.server.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateUserDto(
        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        String phoneNumber
) {
}