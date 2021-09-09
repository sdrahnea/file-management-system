package com.fms.exception;

public class TenantListMissingException extends RuntimeException {

    private static final String MESSAGE = "Tenant list is missing! Provide a configuration!";

    public TenantListMissingException(String message) {
        super(message);
    }

    public TenantListMissingException() {
        super(MESSAGE);
    }

}
