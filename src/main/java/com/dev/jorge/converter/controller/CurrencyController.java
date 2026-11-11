package com.dev.jorge.converter.controller;

import com.dev.jorge.converter.dto.request.ConversionRequest;
import com.dev.jorge.converter.dto.response.ConversionResponse;
import com.dev.jorge.converter.service.CurrencyService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "basicAuth")
public class CurrencyController {
    private final CurrencyService service;

    public CurrencyController(CurrencyService service) { this.service = service; }

    @PostMapping("/conversions")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversionResponse convert(@Valid @RequestBody ConversionRequest request, Authentication authentication) {
        return ConversionResponse.from(
                service.convert(authentication.getName(), request.from(), request.to(), request.amount())
        );
    }

    @GetMapping("/conversions")
    @ResponseStatus(HttpStatus.OK)
    public List<ConversionResponse> history(Authentication authentication) {
        return service.history(authentication.getName()).stream().map(ConversionResponse::from).toList();
    }

    @GetMapping("/rates")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, BigDecimal> rates(@RequestParam(defaultValue = "USD") String baseCode) {
        return service.supportedRates(baseCode);
    }

    @GetMapping("/currencies")
    @ResponseStatus(HttpStatus.OK)
    public List<String> currencies(@RequestParam(defaultValue = "USD") String baseCode) {
        return service.currencies(baseCode);
    }
}
