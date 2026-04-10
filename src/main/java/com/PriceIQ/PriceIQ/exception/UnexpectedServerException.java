package com.PriceIQ.PriceIQ.exception;

public class UnexpectedServerException extends RuntimeException {
    public UnexpectedServerException(String message) {
        super(message);
    }

    public UnexpectedServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
