package com.osigie.eazi_wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigInteger;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseModel {

    @Column(name = "balance_cached", nullable = false)
    private BigInteger balanceCached;

    @Column(name = "currency", nullable = false, length = 3)
    private String currencyCode;


    public void updateBalanceCache(BigInteger data) {
        BigInteger next = balanceCached.add(data);
        if (next.signum() < 0) {
            throw new IllegalStateException("Balance cannot go negative");
        }
        this.balanceCached = next;
    }


    @Builder
    public Wallet(BigInteger balanceCached, String currencyCode) {
        this.balanceCached = BigInteger.ZERO;
        this.currencyCode = currencyCode;
    }

}
