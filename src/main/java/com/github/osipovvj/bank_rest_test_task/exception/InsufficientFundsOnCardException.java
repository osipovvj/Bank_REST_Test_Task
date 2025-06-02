package com.github.osipovvj.bank_rest_test_task.exception;

public class InsufficientFundsOnCardException extends RuntimeException {

    public InsufficientFundsOnCardException() {}

    public InsufficientFundsOnCardException(String message) {
        super(message);
    }
}
