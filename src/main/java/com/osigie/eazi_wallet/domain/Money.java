package com.osigie.eazi_wallet.domain;

import com.osigie.eazi_wallet.exception.CurrencyMismatchException;
import com.osigie.eazi_wallet.exception.InsufficientFundsException;

import java.math.BigInteger;

public record Money(BigInteger amount, String currency) {

    public Money {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
    }


    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        BigInteger result = this.amount.subtract(other.amount);
        if (result.signum() < 0) {
            throw new InsufficientFundsException(amount, other.amount);

        }
        return new Money(result, currency);
    }


    private void ensureSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(currency, other.currency);
        }
    }
}
