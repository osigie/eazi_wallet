package com.osigie.eazi_wallet.repository;

import com.osigie.eazi_wallet.config.AbstractContainerBaseTest;
import com.osigie.eazi_wallet.domain.EntryTypeEnum;
import com.osigie.eazi_wallet.domain.LedgerEntry;
import com.osigie.eazi_wallet.domain.Wallet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigInteger;

@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LedgerEntryRepositoryTests extends AbstractContainerBaseTest {
    private final LedgerEntryRepository ledgerEntryRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public LedgerEntryRepositoryTests(LedgerEntryRepository ledgerEntryRepository, WalletRepository walletRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.walletRepository = walletRepository;
    }

    private LedgerEntry ledgerEntry;
    private Wallet wallet;
    private static final String KEY = "key";
    private static final String KEY_TWO = "key_2";

    @BeforeEach
    public void setup() {
        wallet = walletRepository.save(Wallet.builder().currencyCode("NGN").balanceCached(BigInteger.TEN).build());
        ledgerEntry = new LedgerEntry(wallet, BigInteger.TEN, EntryTypeEnum.CREDIT, BigInteger.TEN, LedgerEntryRepositoryTests.KEY);
    }

    @Test
    public void given_whenWalletIdAndTypeAndIdempotencyKeyExist_thenShouldReturnTrue() {
        ledgerEntryRepository.save(ledgerEntry);

        boolean result = ledgerEntryRepository.existsByWalletIdAndTypeAndIdempotencyKey(wallet.getId(), EntryTypeEnum.CREDIT, LedgerEntryRepositoryTests.KEY);

        Assertions.assertThat(result).isTrue();

    }


    @Test
    public void given_whenWalletIdAndTypeAndIdempotencyKeyDoesNotExist_thenShouldReturnFalse() {
        ledgerEntry.setIdempotencyKey(LedgerEntryRepositoryTests.KEY_TWO);
        ledgerEntryRepository.save(ledgerEntry);

        boolean result = ledgerEntryRepository.existsByWalletIdAndTypeAndIdempotencyKey(wallet.getId(), EntryTypeEnum.CREDIT, LedgerEntryRepositoryTests.KEY);

        Assertions.assertThat(result).isFalse();

    }
}
