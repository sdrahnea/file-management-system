package com.fms.exception;

public class MissingTenantListException extends RuntimeException {

    private static final String MESSAGE = "Tenant list is missing! Provide a configuration!";

    public MissingTenantListException(String message) {
        super(message);
    }

    public MissingTenantListException() {
        super(MESSAGE);
    }

}
