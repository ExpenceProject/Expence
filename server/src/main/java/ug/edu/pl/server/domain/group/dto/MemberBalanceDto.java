package ug.edu.pl.server.domain.group.dto;

import java.math.BigDecimal;

public record MemberBalanceDto(
        String userId,
        String memberId,
        String memberNickname,
        BigDecimal amount) {}
