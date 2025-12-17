package com.osigie.eazi_wallet.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charges")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Charge extends BaseModel {

    // Percentage in basis points (1 basis point = 0.01%)
    @Column(name = "percentage", nullable = false)
    Long percentage;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    ChargeTypeEnum type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


    @Builder
    public Charge(Long percentage, ChargeTypeEnum type, Boolean isActive) {
        this.percentage = percentage;
        this.type = type;
        this.isActive = isActive;
    }
}
