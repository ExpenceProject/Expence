package ug.edu.pl.server.infrastructure.security.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Must be a valid email address")
        String email,

        @NotBlank(message = "Password must not be blank")
        String password
) {
}
