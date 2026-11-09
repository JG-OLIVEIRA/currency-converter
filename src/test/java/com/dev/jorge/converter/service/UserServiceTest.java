package com.dev.jorge.converter.service;

import com.dev.jorge.converter.entity.User;
import com.dev.jorge.converter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(repository, passwordEncoder);
    }

    @Test
    @DisplayName("Cadastra usuário com senha codificada e chave de API")
    void should_register_user_with_encoded_password_and_api_key() {
        when(passwordEncoder.encode("senha")).thenReturn("senha-codificada");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.signUp("jorge", "senha");

        assertThat(user.getUsername()).isEqualTo("jorge");
        assertThat(user.getPassword()).isEqualTo("senha-codificada");
        assertThat(user.getApiKey()).isNotBlank().hasSize(36);
        verify(passwordEncoder).encode("senha");
        verify(repository).save(user);
    }

    @Test
    @DisplayName("Autentica usuário quando a senha está correta")
    void should_sign_in_user_when_password_matches() {
        User user = new User();
        user.setUsername("jorge");
        user.setPassword("senha-codificada");
        when(repository.findByUsername("jorge")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha", "senha-codificada")).thenReturn(true);

        assertThat(service.signIn("jorge", "senha")).isSameAs(user);
    }

    @Test
    @DisplayName("Rejeita autenticação quando a senha está incorreta")
    void should_reject_sign_in_when_password_does_not_match() {
        User user = new User();
        user.setPassword("senha-codificada");
        when(repository.findByUsername("jorge")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "senha-codificada")).thenReturn(false);

        assertThatThrownBy(() -> service.signIn("jorge", "errada"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");
    }

    @Test
    @DisplayName("Rejeita autenticação quando o usuário não existe")
    void should_reject_sign_in_when_user_does_not_exist() {
        when(repository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.signIn("inexistente", "senha"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");
    }
}
