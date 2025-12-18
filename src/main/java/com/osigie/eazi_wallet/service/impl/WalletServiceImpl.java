package com.osigie.eazi_wallet.service.impl;

import com.osigie.eazi_wallet.domain.*;
import com.osigie.eazi_wallet.exception.DuplicateTransactionException;
import com.osigie.eazi_wallet.exception.InsufficientFundsException;
import com.osigie.eazi_wallet.exception.ResourceNotFoundException;
import com.osigie.eazi_wallet.repository.LedgerEntryRepository;
import com.osigie.eazi_wallet.repository.WalletRepository;
import com.osigie.eazi_wallet.service.RateService;
import com.osigie.eazi_wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.UUID;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final RateService rateService;

    public WalletServiceImpl(WalletRepository walletRepository, LedgerEntryRepository ledgerEntryRepository, RateService rateService) {
        this.walletRepository = walletRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.rateService = rateService;
    }

    @Override
    @Transactional
    public Wallet createWallet(Wallet wallet) {
        long systemInitialBalance = 1_000_000L;

        Money initialFunding = new Money(
                BigInteger.valueOf(systemInitialBalance),
                wallet.getCurrencyCode()
        );

        wallet.setBalanceCached(initialFunding.amount());
        Wallet saveWallet = walletRepository.save(wallet);

        ledgerEntryRepository.save(
                new LedgerEntry(
                        wallet,
                        initialFunding.amount(),
                        EntryTypeEnum.CREDIT,
                        initialFunding.amount(),
                        saveWallet.getId() + "::SYSTEM::INITIAL_FUNDING"
                )
        );

        return wallet;
    }

    @Override
    public Wallet getWallet(UUID id) {
        return walletRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }


    @Override
    @Transactional
    public String topUp(UUID fromWalletId, BigInteger amount, String idempotencyKey) {

        Wallet wallet = lockWallet(fromWalletId);

        Money transactionAmount = new Money(amount, wallet.getCurrencyCode());
        Money currentBalance = new Money(wallet.getBalanceCached(), wallet.getCurrencyCode());

        Money newBalance = currentBalance.add(transactionAmount);

        wallet.setBalanceCached(newBalance.amount());

        saveLedgerEntry(wallet, amount, EntryTypeEnum.CREDIT, newBalance.amount(), idempotencyKey);

        return "Transaction created successfully";
    }

    @Override
    @Transactional
    public String transferFunds(UUID fromWalletId, UUID toWalletId, BigInteger amount, String idempotencyKey) {

        if (fromWalletId.equals(toWalletId)) {
            throw new IllegalArgumentException("You can not transfer to your wallet");
        }

        Wallet sender = lockWallet(fromWalletId);
        Wallet recipient = lockWallet(toWalletId);


        Money transferAmount = new Money(amount, sender.getCurrencyCode());
        Money transferFee = calculateTransferFee(amount, sender.getCurrencyCode());
        Money totalDeduction = transferAmount.add(transferFee);
        Money senderBalance = new Money(sender.getBalanceCached(), sender.getCurrencyCode());

        if (senderBalance.amount().compareTo(totalDeduction.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }

        Money newSenderBalance = senderBalance.subtract(totalDeduction);
        Money balanceAfterTransfer = senderBalance.subtract(transferAmount);
        Money recipientBalance = new Money(recipient.getBalanceCached(), recipient.getCurrencyCode());
        Money newRecipientBalance = recipientBalance.add(transferAmount);

        sender.setBalanceCached(newSenderBalance.amount());
        recipient.setBalanceCached(newRecipientBalance.amount());

        saveLedgerEntry(sender, amount, EntryTypeEnum.DEBIT,
                balanceAfterTransfer.amount(),
                createIdempotencyKeyRef(idempotencyKey, EntryTypeEnum.DEBIT));

        saveLedgerEntry(sender, transferFee.amount(), EntryTypeEnum.CHARGE,
                newSenderBalance.amount(),
                createIdempotencyKeyRef(idempotencyKey, EntryTypeEnum.CHARGE));

        saveLedgerEntry(recipient, amount, EntryTypeEnum.CREDIT,
                newRecipientBalance.amount(),
                createIdempotencyKeyRef(idempotencyKey, EntryTypeEnum.CREDIT));

        return "Transfer completed successfully";
    }

    private Money calculateTransferFee(BigInteger amount, String currencyCode) {
        BigInteger feeAmount = rateService.getRate(amount, ChargeTypeEnum.TRANSFER_FEE);
        return new Money(feeAmount, currencyCode);
    }

    private Wallet lockWallet(UUID walletId) {
        return walletRepository.selectByIdForUpdate(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
    }

    private void saveLedgerEntry(Wallet wallet, BigInteger amount, EntryTypeEnum type,
                                 BigInteger balanceAfter, String idempotencyKey) {

        if (ledgerEntryRepository.existsByWalletIdAndTypeAndIdempotencyKey(
                wallet.getId(), type, idempotencyKey)) {
            throw new DuplicateTransactionException(idempotencyKey);
        }

        try {
            LedgerEntry entry = new LedgerEntry(wallet, amount, type, balanceAfter, idempotencyKey);
            ledgerEntryRepository.save(entry);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateTransactionException(idempotencyKey);
        }

    }


    private String createIdempotencyKeyRef(String key, EntryTypeEnum type) {
        return key.trim() + "::" + type.name();
    }
}
