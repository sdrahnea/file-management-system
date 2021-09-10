package com.fms.exception;

public class MissingFileDbLocationException extends RuntimeException {

    private static final String MESSAGE = "Value for file.db.location property is missing!";

    public MissingFileDbLocationException(String message) {
        super(message);
    }

    public MissingFileDbLocationException() {
        super(MESSAGE);
    }

}
