package com.fms.exception;

public class TenantIsNotAllowedException extends RuntimeException {

    private static final String MESSAGE = "Tenant is not allowed!";

    public TenantIsNotAllowedException(String message) {
        super(message);
    }

    public TenantIsNotAllowedException() {
        super(MESSAGE);
    }

}
