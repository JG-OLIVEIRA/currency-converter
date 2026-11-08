package com.dev.jorge.converter.dto.response;

import com.dev.jorge.converter.entity.Conversion;
import java.math.BigDecimal;
import java.time.Instant;

public record ConversionResponse(Long id, String from, String to, BigDecimal amount,
                                 BigDecimal rate, BigDecimal result, Instant createdAt) {
    public static ConversionResponse from(Conversion conversion) {
        return new ConversionResponse(conversion.getId(), conversion.getFromCurrency(),
                conversion.getToCurrency(), conversion.getAmount(), conversion.getRate(),
                conversion.getResult(), conversion.getCreatedAt());
    }
}
