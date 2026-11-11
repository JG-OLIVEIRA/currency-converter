package com.dev.jorge.converter.handler;

import com.dev.jorge.converter.dto.response.ExceptionResponse;
import com.dev.jorge.converter.exceptions.UserNotFoundException;
import com.dev.jorge.converter.exceptions.UserAlreadyExistsException;
import com.dev.jorge.converter.exceptions.UnsupportedCurrencyException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

        @ExceptionHandler(BadCredentialsException.class)
        public final ResponseEntity<ExceptionResponse> handlerBadRequestException(
                        Exception ex, WebRequest webRequest) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(
                                ex.getMessage(),
                                webRequest.getDescription(false),
                                new Date());
                return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public final ResponseEntity<ExceptionResponse> handlerUserNotFoundException(
                        Exception ex, WebRequest webRequest) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(
                                ex.getMessage(),
                                webRequest.getDescription(false),
                                new Date());
                return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public final ResponseEntity<ExceptionResponse> handlerUserAlreadyExistsException(
                        Exception ex, WebRequest webRequest) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(
                                ex.getMessage(),
                                webRequest.getDescription(false),
                                new Date());
                return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(UnsupportedCurrencyException.class)
        public final ResponseEntity<ExceptionResponse> handlerUnsupportedCurrencyException(
                        Exception ex, WebRequest webRequest) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(
                                ex.getMessage(),
                                webRequest.getDescription(false),
                                new Date());
                return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public final ResponseEntity<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                        WebRequest webRequest) {

                List<Map<String, String>> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> {
                                        Map<String, String> err = new HashMap<>();
                                        err.put("field", error.getField());
                                        err.put("message", error.getDefaultMessage());
                                        return err;
                                })
                                .toList();

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Erro de validação");
                response.put("errors", errors);
                response.put("details", webRequest.getDescription(false));
                response.put("timestamp", new Date());

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

}