package com.dev.jorge.converter.dto.response;

import java.util.Date;

public record ExceptionResponse(String message, String details, Date timestamp) {

}