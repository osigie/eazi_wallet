package com.osigie.eazi_wallet.service.impl;

import com.osigie.eazi_wallet.domain.Charge;
import com.osigie.eazi_wallet.domain.ChargeTypeEnum;
import com.osigie.eazi_wallet.repository.ChargeRepository;
import com.osigie.eazi_wallet.service.RateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@Slf4j
public class RateServiceImpl implements RateService {
    private final ChargeRepository chargeRepository;

    public RateServiceImpl(ChargeRepository chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    @Override
    public BigInteger getRate(BigInteger amount, ChargeTypeEnum chargeType) {

        Charge charge = chargeRepository.findByTypeAndIsActive(chargeType, true)
                .orElseThrow(() -> new RuntimeException("Charge not found"));

        BigInteger rate = this.calculateByBasisPoints(amount, charge.getPercentage());

        log.debug("Calculated rate {} for amount {} with charge type {}",
                rate, amount, chargeType);

        return rate;
    }

    private BigInteger calculateByBasisPoints(BigInteger amount, BigDecimal basisPoints) {
        return amount.multiply(BigInteger.valueOf(basisPoints.longValue()))
                .divide(BigInteger.valueOf(10_000));
    }
}
