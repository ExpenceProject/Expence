package ug.edu.pl.server.domain.group.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record CreateBillDto(
        @NotBlank(message = "Name must not be blank") String name,

        @NotNull(message = "Expenses must not be null")
        @Size(min = 1, message = "At least one expense must be provided")
        Set<CreateExpenseDto> expenses,

        @NotNull(message = "Total amount must not be null")
        @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Total amount must have at most 10 digits in the integer part and 2 in the fractional part")
        BigDecimal totalAmount,

        @NotNull(message = "Lender ID must not be null")
        @Positive(message = "Lender ID must be a positive number")
        Long lenderId,

        @NotNull(message = "Lender ID must not be null")
        @Positive(message = "Lender ID must be a positive number")
        Long groupId
    ) {}
