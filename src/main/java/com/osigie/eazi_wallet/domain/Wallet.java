package com.osigie.eazi_wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseModel {

    @Column(name = "balance_cached", nullable = false)
    BigDecimal balanceCached;

    @Builder
    public Wallet(BigDecimal balanceCached) {
        this.balanceCached = balanceCached;
    }
}
