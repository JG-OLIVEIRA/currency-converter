package com.dev.jorge.converter.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotBlank @Size(min = 3, max = 3) String from,
        @NotBlank @Size(min = 3, max = 3) String to,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount) {
}
