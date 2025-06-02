package com.github.osipovvj.bank_rest_test_task.exception;

public class InternalServerException extends RuntimeException {

    public InternalServerException() {
        super();
    }

    public InternalServerException(final String message) {
        super(message);
    }
}
