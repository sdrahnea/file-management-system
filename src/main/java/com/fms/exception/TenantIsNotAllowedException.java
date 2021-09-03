package com.fms.exception;

public class TenantIsNotAllowedException extends RuntimeException {

    public TenantIsNotAllowedException(String message) {
        super(message);
    }

}
