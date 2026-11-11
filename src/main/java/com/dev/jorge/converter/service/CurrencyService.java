package com.dev.jorge.converter.service;

import com.dev.jorge.converter.client.ExchangeRatesClient;
import com.dev.jorge.converter.dto.response.RatesResponse;
import com.dev.jorge.converter.entity.Conversion;
import com.dev.jorge.converter.repository.ConversionRepository;
import com.dev.jorge.converter.exceptions.UnsupportedCurrencyException;
import org.springframework.stereotype.Service;
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
        BigDecimal result = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
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
        RatesResponse response = client.getRates(baseCode);
        if (response.result().equals("error")) {
            throw new UnsupportedCurrencyException("Unsupported currency: " + baseCode);
        }
        return response;
    }

    private String normalize(String baseCode) {
        return baseCode.trim().toUpperCase(Locale.ROOT);
    }

}
