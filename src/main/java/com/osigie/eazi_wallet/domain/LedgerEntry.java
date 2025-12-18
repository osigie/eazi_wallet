package com.osigie.eazi_wallet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "ledger_entries", uniqueConstraints = {
        @UniqueConstraint(name = "unique_idempotency_key", columnNames = "idempotency_key")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry extends BaseModel {

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(name = "balance", nullable = false)
    private BigInteger amount;


    @Column(name = "balance_after", nullable = false)
    private BigInteger balanceAfter;


    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntryTypeEnum type;


    public LedgerEntry(Wallet wallet, BigInteger money, EntryTypeEnum type, BigInteger balanceAfter, String key) {
        this.wallet = wallet;
        this.amount = money;
        this.type = type;
        this.balanceAfter = balanceAfter;
        this.idempotencyKey = key;
    }
}
