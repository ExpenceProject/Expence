package ug.edu.pl.server.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserDto(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Must be a valid email address")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be min 8 characters long")
        String password,

        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        String phoneNumber
) {
}
