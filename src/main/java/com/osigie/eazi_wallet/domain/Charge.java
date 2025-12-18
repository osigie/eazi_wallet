package com.osigie.eazi_wallet.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "charges")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Charge extends BaseModel {

    // Percentage in basis points (1 basis point = 0.01%)
    @Column(name = "percentage", nullable = false, precision = 10, scale = 4)
    private BigDecimal percentage;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChargeTypeEnum type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


    @Builder
    public Charge(BigDecimal percentage, ChargeTypeEnum type, Boolean isActive) {
        this.percentage = percentage;
        this.type = type;
        this.isActive = isActive;
    }
}
