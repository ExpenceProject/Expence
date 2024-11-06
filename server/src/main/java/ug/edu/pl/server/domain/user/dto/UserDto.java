package ug.edu.pl.server.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import ug.edu.pl.server.domain.common.storage.ImageDto;

import java.time.Instant;
import java.util.Set;

@Builder
public record UserDto(Long id, String email, @JsonIgnore String password, String firstName, String lastName, String phoneNumber,
                      ImageDto image, Set<RoleDto> roles, Long version, Instant createdAt, Instant updatedAt) {
}
