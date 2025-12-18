package com.osigie.eazi_wallet.service;

import com.osigie.eazi_wallet.domain.*;
import com.osigie.eazi_wallet.exception.DuplicateTransactionException;
import com.osigie.eazi_wallet.exception.InsufficientFundsException;
import com.osigie.eazi_wallet.repository.LedgerEntryRepository;
import com.osigie.eazi_wallet.repository.WalletRepository;
import com.osigie.eazi_wallet.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class WalletServiceTests {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private RateService rateService;

    @InjectMocks
    private WalletServiceImpl walletService;

    private LedgerEntry ledgerEntry;
    private Wallet wallet;
    private UUID walletId;
    private static final BigInteger INITIAL_BALANCE = BigInteger.TEN;
    private static final String CURRENCY = "NGN";

    @BeforeEach
    public void setup() {
        walletId = UUID.randomUUID();

        Wallet realWallet = Wallet.builder()
                .currencyCode(CURRENCY)
                .balanceCached(INITIAL_BALANCE)
                .build();

        wallet = spy(realWallet);

        doReturn(walletId).when(wallet).getId();

        ledgerEntry = new LedgerEntry(wallet, BigInteger.TEN, EntryTypeEnum.CREDIT, BigInteger.TEN, "key");
    }

    @Test
    public void givenWalletDetails_whenCreateWalletIsCalled_thenShouldCreateWalletAndCreateLedgerEntry() {
        given(walletRepository.save(wallet)).willReturn(wallet);
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenReturn(ledgerEntry);

        Wallet result = walletService.createWallet(wallet);

        verify(walletRepository, times(1)).save(wallet);
        verify(ledgerEntryRepository, atMostOnce()).save(any(LedgerEntry.class));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(wallet.getCurrencyCode(), result.getCurrencyCode());
        Assertions.assertEquals(BigInteger.valueOf(1_000_000L), result.getBalanceCached());
    }

    @Test
    public void givenValidTopUpRequest_whenTopUpIsCalled_thenBalanceIsUpdatedAndLedgerEntrySaved() {
        BigInteger topUpAmount = BigInteger.valueOf(50);
        String idempotencyKey = "topup-key";

        given(walletRepository.selectByIdForUpdate(walletId)).willReturn(Optional.of(wallet));
        when(ledgerEntryRepository.existsByWalletIdAndTypeAndIdempotencyKey(any(), any(), any())).thenReturn(false);
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenReturn(ledgerEntry);

        String result = walletService.topUp(walletId, topUpAmount, idempotencyKey);

        verify(walletRepository, times(1)).selectByIdForUpdate(walletId);
        verify(ledgerEntryRepository, times(1)).save(any(LedgerEntry.class));
        Assertions.assertEquals("Transaction created successfully", result);
        Assertions.assertEquals(wallet.getBalanceCached(), BigInteger.valueOf(60));
    }

    @Test
    public void givenValidTransfer_whenTransferFundsIsCalled_thenBalancesUpdatedAndLedgerEntriesSaved() {
        UUID senderId = walletId;
        UUID recipientId = UUID.randomUUID();

        Wallet recipientWallet = Wallet.builder()
                .currencyCode(CURRENCY)
                .balanceCached(BigInteger.valueOf(20))
                .build();

        given(walletRepository.selectByIdForUpdate(senderId)).willReturn(Optional.of(wallet));
        given(walletRepository.selectByIdForUpdate(recipientId)).willReturn(Optional.of(recipientWallet));

        BigInteger transferAmount = BigInteger.valueOf(5);
        String idempotencyKey = "transfer-key";

        given(rateService.getRate(transferAmount, ChargeTypeEnum.TRANSFER_FEE)).willReturn(BigInteger.ONE);
        when(ledgerEntryRepository.existsByWalletIdAndTypeAndIdempotencyKey(any(), any(), any())).thenReturn(false);
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = walletService.transferFunds(senderId, recipientId, transferAmount, idempotencyKey);

        verify(walletRepository).selectByIdForUpdate(senderId);
        verify(walletRepository).selectByIdForUpdate(recipientId);

        verify(ledgerEntryRepository, times(3)).save(any(LedgerEntry.class));

        Assertions.assertEquals("Transfer completed successfully", result);

        BigInteger expectedSenderBalance = INITIAL_BALANCE.subtract(transferAmount).subtract(BigInteger.ONE);
        BigInteger expectedRecipientBalance = BigInteger.valueOf(20).add(transferAmount);

        Assertions.assertEquals(expectedSenderBalance, wallet.getBalanceCached());
        Assertions.assertEquals(expectedRecipientBalance, recipientWallet.getBalanceCached());
    }

    @Test
    public void givenInsufficientFunds_whenTransferFundsIsCalled_thenThrowInsufficientFundsException() {
        UUID senderId = walletId;
        UUID recipientId = UUID.randomUUID();

        Wallet recipientWallet = Wallet.builder()
                .currencyCode("NGN")
                .balanceCached(BigInteger.valueOf(20))
                .build();

        BigInteger transferAmount = BigInteger.valueOf(50);
        String idempotencyKey = "transfer-key";

        given(walletRepository.selectByIdForUpdate(senderId)).willReturn(Optional.of(wallet));
        given(walletRepository.selectByIdForUpdate(recipientId)).willReturn(Optional.of(recipientWallet));
        given(rateService.getRate(transferAmount, ChargeTypeEnum.TRANSFER_FEE)).willReturn(BigInteger.ONE);

        Assertions.assertThrows(InsufficientFundsException.class,
                () -> walletService.transferFunds(senderId, recipientId, transferAmount, idempotencyKey));
    }

    @Test
    public void givenDuplicateTransaction_whenSavingLedgerEntry_thenThrowDuplicateTransactionException() {
        String idempotencyKey = "dup-key";

        when(walletRepository.selectByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));

        when(ledgerEntryRepository.existsByWalletIdAndTypeAndIdempotencyKey(walletId, EntryTypeEnum.CREDIT, idempotencyKey))
                .thenReturn(true);


        Assertions.assertThrows(DuplicateTransactionException.class,
                () -> walletService.topUp(walletId, BigInteger.TEN, idempotencyKey));
    }
}
