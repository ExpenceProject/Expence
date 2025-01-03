package ug.edu.pl.server.domain.group.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentDto(
        @NotNull(message = "Sender ID must not be null")
        @Positive(message = "Sender ID must be a positive number")
        Long senderId,
        @NotNull(message = "Receiver ID must not be null")
        @Positive(message = "Receiver ID must be a positive number")
        Long receiverId,
        @NotNull(message = "Amount must not be null")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits in the integer part and 2 in the fractional part")
        BigDecimal amount,
        @NotNull(message = "Group ID must not be null")
        @Positive(message = "Group ID must be a positive number")
        Long groupId
) {}