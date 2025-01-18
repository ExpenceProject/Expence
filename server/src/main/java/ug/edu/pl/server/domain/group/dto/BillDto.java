package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record BillDto(String id,
                      String name,
                      Set<ExpenseDto> expenses,
                      BigDecimal totalAmount,
                      MemberDto lender,
                      String groupId,
                      Long version,
                      Instant createdAt,
                      Instant updatedAt) {}
