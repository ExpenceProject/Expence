package ug.edu.pl.server.infrastructure.security.auth.dto;

import lombok.Builder;
import ug.edu.pl.server.domain.user.dto.UserDto;

@Builder
public record AuthDto(String tokenType, String token, UserDto user) {
    public AuthDto(String token, UserDto user) {
        this("Bearer", token, user);
    }
}
