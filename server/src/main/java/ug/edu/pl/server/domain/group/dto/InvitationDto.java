package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record InvitationDto(Long id,
                            Long inviteeId,
                            Long inviterId,
                            Long groupId,
                            String status,
                            Long version,
                            Instant createdAt,
                            Instant updatedAt) {}
