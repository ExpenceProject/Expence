package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;
import java.time.Instant;

@Builder
public record MemberDto(
    Long id,
    Long user,
    String nickname,
    GroupRoleDto groupRole,
    Long version,
    Instant createdAt,
    Instant updatedAt) {}
