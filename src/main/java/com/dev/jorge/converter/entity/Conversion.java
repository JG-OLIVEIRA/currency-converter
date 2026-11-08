package com.dev.jorge.converter.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_conversion_history")
public class Conversion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 3)
    private String fromCurrency;

    @Column(nullable = false, length = 3)
    private String toCurrency;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal result;

    @Column(nullable = false)
    private Instant createdAt;

    protected Conversion() {}

    public Conversion(String username, String fromCurrency, String toCurrency,
                      BigDecimal amount, BigDecimal rate, BigDecimal result) {
        this.username = username;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.rate = rate;
        this.result = result;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getFromCurrency() { return fromCurrency; }

    public String getToCurrency() { return toCurrency; }

    public BigDecimal getAmount() { return amount; }

    public BigDecimal getRate() { return rate; }

    public BigDecimal getResult() { return result; }

    public Instant getCreatedAt() { return createdAt; }
}
