package com.fms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class StorageQuotaExceededException extends RuntimeException {
    public StorageQuotaExceededException(String tenant, long limitBytes) {
        super("Storage quota exceeded for tenant '" + tenant + "'. Limit: " + limitBytes + " bytes.");
    }
}

