package com.osigie.eazi_wallet.domain;

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
            throw new IllegalStateException("Insufficient funds");
        }
        return new Money(result, currency);
    }


    private void ensureSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }
}
