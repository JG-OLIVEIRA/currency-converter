package com.dev.jorge.converter;

import com.dev.jorge.converter.client.ExchangeRatesClient;
import com.dev.jorge.converter.dto.response.RatesResponse;
import com.dev.jorge.converter.repository.ConversionRepository;
import com.dev.jorge.converter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CurrencyConverterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversionRepository conversionRepository;

    @MockitoBean
    private ExchangeRatesClient exchangeRatesClient;

    @BeforeEach
    void cleanDatabase() {
        conversionRepository.deleteAll();
        userRepository.deleteAll();
        when(exchangeRatesClient.getRates("USD")).thenReturn(new RatesResponse("success", "USD",
                Map.of("BRL", new BigDecimal("5.00"), "EUR", new BigDecimal("0.90"))));
    }

    @Test
    @DisplayName("Cadastra usuário e autentica conversão por chave de API")
    void should_register_and_convert_when_api_key_is_valid() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content("""
                                {"username":"jorge","password":"senha"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("jorge"))
                .andExpect(jsonPath("$.apiKey").isString())
                .andReturn().getResponse().getContentAsString();
        String apiKey = com.jayway.jsonpath.JsonPath.read(response, "$.apiKey");

        mockMvc.perform(put("/api/users")
                        .contentType("application/json")
                        .content("""
                                {"username":"jorge","password":"senha"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKey").value(apiKey));

        mockMvc.perform(post("/api/conversions")
                        .header("X-API-Key", apiKey)
                        .contentType("application/json")
                        .content("""
                                {"from":" usd ","to":"brl","amount":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("BRL"))
                .andExpect(jsonPath("$.result").value(10.0));

        mockMvc.perform(get("/api/conversions").header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").doesNotExist())
                .andExpect(jsonPath("$[0].from").value("USD"));
    }

    @Test
    @DisplayName("Bloqueia endpoints protegidos sem autenticação")
    void should_reject_protected_endpoint_without_api_key() throws Exception {
        mockMvc.perform(get("/api/conversions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Retorna taxas públicas e valida payload obrigatório")
    void should_return_rates_and_reject_invalid_conversion_payload() throws Exception {
        mockMvc.perform(get("/api/rates").param("baseCode", "usd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.BRL").value(5.0));

        String apiKey = registerUser("valid-user");
        mockMvc.perform(post("/api/conversions")
                        .header("X-API-Key", apiKey)
                        .contentType("application/json")
                        .content("""
                                {"from":"USD","to":"BRL","amount":-1}
                                """))
                .andExpect(status().isBadRequest());
    }

    private String registerUser(String username) throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content("""
                                {"username":"%s","password":"senha"}
                                """.formatted(username)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.apiKey");
    }
}
