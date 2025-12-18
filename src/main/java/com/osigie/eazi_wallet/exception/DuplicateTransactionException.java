package com.osigie.eazi_wallet.exception;

public class DuplicateTransactionException extends RuntimeException {


    public DuplicateTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateTransactionException(String idempotencyKey) {
        super("Duplicate transaction detected for idempotency key: " + idempotencyKey);
    }
}
