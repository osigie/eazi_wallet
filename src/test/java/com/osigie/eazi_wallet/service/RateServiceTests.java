package com.osigie.eazi_wallet.service;

import com.osigie.eazi_wallet.domain.Charge;
import com.osigie.eazi_wallet.domain.ChargeTypeEnum;
import com.osigie.eazi_wallet.repository.ChargeRepository;
import com.osigie.eazi_wallet.service.impl.RateServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class RateServiceTests {

    @Mock
    private ChargeRepository chargeRepository;

    @InjectMocks
    private RateServiceImpl rateService;

    private Charge charge;

    @BeforeEach
    public void setup() {
        charge = Charge.builder()
                .type(ChargeTypeEnum.TRANSFER_FEE)
                .percentage(BigDecimal.valueOf(1L))
                .build();
    }

    @Test
    public void givenAmountAndChargeType_whenCharge_thenShouldReturnRate() {

        given(chargeRepository.findByTypeAndIsActive(charge.getType(), true)).willReturn(Optional.of(charge));
        BigInteger amount = BigInteger.valueOf(1_000_000L);

        BigInteger rate = rateService.getRate(amount, charge.getType());

        Assertions.assertNotNull(rate);
        Assertions.assertEquals(BigInteger.valueOf(100L), rate);


    }
}
