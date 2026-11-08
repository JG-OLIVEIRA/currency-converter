package com.dev.jorge.converter.client;

import com.dev.jorge.converter.dto.response.RatesResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ExchangeRatesClient {

    @GetExchange("/{baseCode}")
    RatesResponse getRates(@PathVariable String baseCode);
}
