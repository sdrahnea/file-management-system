package com.fms.exception;

public class MissingFileIdStrategyException extends RuntimeException {

    private static final String MESSAGE = "Value for file.id.strategy property is missing! Please, provide a value!";

    public MissingFileIdStrategyException(String message) {
        super(message);
    }

    public MissingFileIdStrategyException() {
        super(MESSAGE);
    }

}
