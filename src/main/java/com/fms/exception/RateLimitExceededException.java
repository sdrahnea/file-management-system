package com.fms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String tenant, long limitPerDay) {
        super("Upload rate limit exceeded for tenant '" + tenant + "'. Max uploads per day: " + limitPerDay + ".");
    }
}

