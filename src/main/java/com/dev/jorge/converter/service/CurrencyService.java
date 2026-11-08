package com.dev.jorge.converter.service;

import com.dev.jorge.converter.client.ExchangeRatesClient;
import com.dev.jorge.converter.dto.response.RatesResponse;
import com.dev.jorge.converter.entity.Conversion;
import com.dev.jorge.converter.repository.ConversionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CurrencyService {

    private final ConversionRepository repository;
    private final ExchangeRatesClient client;

    public CurrencyService(ConversionRepository repository, ExchangeRatesClient client) {
        this.repository = repository;
        this.client = client;
    }

    public Conversion convert(String username, String from, String to, BigDecimal amount) {
        String baseCode = normalize(from);
        String target = normalize(to);
        RatesResponse response = rates(baseCode);
        BigDecimal rate = response.rates().get(target);
        if (rate == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Unsupported currency: " + target);
        }
        BigDecimal result = amount.multiply(rate).setScale(6, RoundingMode.HALF_UP);
        return repository.save(new Conversion(username, baseCode, target, amount, rate, result));
    }

    public Map<String, BigDecimal> supportedRates(String baseCode) {
        return rates(normalize(baseCode)).rates();
    }

    public List<String> currencies(String baseCode) {
        return new ArrayList<>(supportedRates(baseCode).keySet()).stream().sorted().toList();
    }

    public List<Conversion> history(String username) {
        return repository.findByUsernameOrderByCreatedAtDesc(username);
    }

    private RatesResponse rates(String baseCode) {
        return client.getRates(baseCode);
    }

    private String normalize(String currency) {
        return currency.trim().toUpperCase(Locale.ROOT);
    }

}
