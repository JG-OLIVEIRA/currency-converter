package com.dev.jorge.converter.service;

import com.dev.jorge.converter.client.ExchangeRatesClient;
import com.dev.jorge.converter.dto.response.RatesResponse;
import com.dev.jorge.converter.entity.Conversion;
import com.dev.jorge.converter.repository.ConversionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private ConversionRepository repository;

    @Mock
    private ExchangeRatesClient client;

    private CurrencyService service;

    @BeforeEach
    void setUp() {
        service = new CurrencyService(repository, client);
    }

    @Test
    @DisplayName("Converte o valor normalizando as moedas e arredondando o resultado")
    void should_convert_amount_when_rate_exists() {
        when(client.getRates("USD")).thenReturn(new RatesResponse("success", "USD",
                Map.of("BRL", new BigDecimal("5.1234567"))));
        when(repository.save(any(Conversion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Conversion conversion = service.convert("jorge", " usd ", "brl", new BigDecimal("2"));

        assertThat(conversion.getUsername()).isEqualTo("jorge");
        assertThat(conversion.getFromCurrency()).isEqualTo("USD");
        assertThat(conversion.getToCurrency()).isEqualTo("BRL");
        assertThat(conversion.getRate()).isEqualByComparingTo("5.1234567");
        assertThat(conversion.getResult()).isEqualByComparingTo("10.246913");
        verify(client).getRates("USD");
        verify(repository).save(conversion);
    }

    @Test
    @DisplayName("Rejeita conversão quando a moeda de destino não é suportada")
    void should_reject_conversion_when_target_currency_is_not_supported() {
        when(client.getRates("USD")).thenReturn(new RatesResponse("success", "USD",
                Map.of("BRL", BigDecimal.ONE)));

        assertThatThrownBy(() -> service.convert("jorge", "USD", "EUR", BigDecimal.ONE))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Unsupported currency: EUR");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Retorna as moedas suportadas ordenadas")
    void should_return_sorted_currencies_when_rates_are_available() {
        when(client.getRates("EUR")).thenReturn(new RatesResponse("success", "EUR",
                Map.of("USD", BigDecimal.ONE, "BRL", BigDecimal.TEN, "ARS", BigDecimal.ONE)));

        assertThat(service.currencies("eur")).containsExactly("ARS", "BRL", "USD");
    }

    @Test
    @DisplayName("Consulta o histórico do usuário")
    void should_return_user_history() {
        List<Conversion> history = List.of();
        when(repository.findByUsernameOrderByCreatedAtDesc("jorge")).thenReturn(history);

        assertThat(service.history("jorge")).isSameAs(history);
    }
}
