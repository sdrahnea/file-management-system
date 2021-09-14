package com.fms.exception;

public class MissingStorageStrategyException extends RuntimeException {

    private static final String MESSAGE = "Storage strategy is missing! Provide a configuration for ${storage.strategy} property!";

    public MissingStorageStrategyException(String message) {
        super(message);
    }

    public MissingStorageStrategyException() {
        super(MESSAGE);
    }

}
