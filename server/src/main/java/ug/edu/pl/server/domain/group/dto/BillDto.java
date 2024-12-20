package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record BillDto(Long id,
                      String name,
                      Set<ExpenseDto> expenses,
                      BigDecimal totalAmount,
                      MemberDto lender,
                      Long groupId,
                      Long version,
                      Instant createdAt,
                      Instant updatedAt) {}
