package ug.edu.pl.server.domain.group.dto;

import java.math.BigDecimal;

public record MemberBalanceDto(
        Long userId,
        Long memberId,
        String memberNickname,
        BigDecimal amount) {}
