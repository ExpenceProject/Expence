package ug.edu.pl.server.domain.group.dto;

import java.math.BigDecimal;

public record ExpenseDto(MemberDto borrower, BigDecimal amount) {}
