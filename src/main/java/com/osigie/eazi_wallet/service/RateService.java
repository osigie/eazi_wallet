package com.osigie.eazi_wallet.service;

import com.osigie.eazi_wallet.domain.ChargeTypeEnum;

import java.math.BigInteger;

public interface RateService {

    BigInteger getRate(BigInteger amount, ChargeTypeEnum chargeType);
}
