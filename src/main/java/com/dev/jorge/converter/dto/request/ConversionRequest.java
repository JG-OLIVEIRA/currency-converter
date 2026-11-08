package com.dev.jorge.converter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ConversionRequest(
        @NotBlank String from,
        @NotBlank String to,
        @NotNull @Min(0L) BigDecimal amount
) {}
