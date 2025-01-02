package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentDto(
        Long id,
        MemberDto receiver,
        MemberDto sender,
        BigDecimal amount,
        GroupDto group,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}