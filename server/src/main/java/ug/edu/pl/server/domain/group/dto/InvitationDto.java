package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record InvitationDto(String id,
                            String inviteeId,
                            String inviterId,
                            String groupId,
                            String status,
                            Long version,
                            Instant createdAt,
                            Instant updatedAt) {}
