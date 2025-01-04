package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;
import java.time.Instant;

@Builder
public record MemberDto(
    String id,
    String user,
    String nickname,
    GroupRoleDto groupRole,
    Long version,
    Instant createdAt,
    Instant updatedAt) {}
