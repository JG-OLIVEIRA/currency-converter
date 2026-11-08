package com.dev.jorge.converter.config;

import com.dev.jorge.converter.client.ExchangeRatesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${exchange-rates.url}")
    private String exchangeRatesUrl;

    @Bean
    public ExchangeRatesClient inventoryClient() {
        RestClient restClient = RestClient.builder().baseUrl(exchangeRatesUrl).build();

        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpServiceProxyFactory.createClient(ExchangeRatesClient.class);
    }
}
