package ug.edu.pl.server.domain.group.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateExpenseDto(
        @NotNull(message = "Lender ID must not be null")
        @Positive(message = "Lender ID must be a positive number")
        Long borrowerId,

        @NotNull(message = "Amount must not be null")
        @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
        @Digits(integer = 6, fraction = 2, message = "Total amount must have at most 6 digits in the integer part and 2 in the fractional part")
        BigDecimal amount
) {}
