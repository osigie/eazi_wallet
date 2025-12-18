package com.osigie.eazi_wallet.exception;

import java.math.BigInteger;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientFundsException(BigInteger required, BigInteger available) {
        super(String.format("Insufficient funds. Required: %s, Available: %s", required, available));
    }
}
