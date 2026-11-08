package com.dev.jorge.converter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 36)
    private String apiKey;

    public void setId(Long id) { this.id = id; }

    public Long getId() {   return id; }

    public void setUsername(String username){ this.username = username; }

    public String getUsername(){ return username; }

    public void setPassword(String password) { this.password = password; }

    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }

    public String getPassword() { return password; }
}
