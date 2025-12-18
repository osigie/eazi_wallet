package com.osigie.eazi_wallet.exception;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(String message) {
        super(message);
    }

    public CurrencyMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyMismatchException(String expected, String actual) {
        super(String.format("Currency mismatch: expected %s, Got: %s", expected, actual));
    }
}
