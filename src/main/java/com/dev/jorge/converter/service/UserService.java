package com.dev.jorge.converter.service;

import com.dev.jorge.converter.entity.User;
import com.dev.jorge.converter.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUp(String username, String password){
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setApiKey(UUID.randomUUID().toString());
        return repository.save(newUser);
    }

    public User signIn(String username, String password){
        User user = findByUsername(username);
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return user;
    }

    private User findByUsername(String username){
        return repository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
