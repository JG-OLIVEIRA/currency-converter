package com.dev.jorge.converter.controller;

import com.dev.jorge.converter.dto.request.RegisterUserRequest;
import com.dev.jorge.converter.dto.response.LoginUserResponse;
import com.dev.jorge.converter.dto.response.RegisterUserResponse;
import com.dev.jorge.converter.entity.User;
import com.dev.jorge.converter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse register(@RequestBody @Valid RegisterUserRequest request){
        User user = service.signUp(request.username(), request.password());
        return new RegisterUserResponse(user.getUsername(), user.getApiKey());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LoginUserResponse login(@RequestBody @Valid RegisterUserRequest request){
        User user = service.signIn(request.username(), request.password());
        return new LoginUserResponse(user.getApiKey());
    }
}
