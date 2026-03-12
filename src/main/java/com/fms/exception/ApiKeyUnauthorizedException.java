package com.fms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ApiKeyUnauthorizedException extends RuntimeException {
    public ApiKeyUnauthorizedException() {
        super("Invalid or missing API key. Provide a valid X-API-Key header.");
    }
}

