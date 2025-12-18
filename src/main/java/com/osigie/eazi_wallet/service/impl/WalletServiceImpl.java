package com.osigie.eazi_wallet.service.impl;

import com.osigie.eazi_wallet.domain.*;
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
    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public String createTransaction(UUID fromWalletId, BigInteger amount, EntryTypeEnum type, String IdempotencyKey) {
        Wallet wallet = walletRepository.selectByIdForUpdate(fromWalletId).orElseThrow(() -> new RuntimeException("Wallet not found"));

        Money money = new Money(amount, wallet.getCurrencyCode());

        BigInteger signed = type == EntryTypeEnum.CREDIT ? money.amount() : money.amount().negate();

        wallet.updateBalanceCache(signed);

        try {
            ledgerEntryRepository.save(new LedgerEntry(wallet, signed, type, wallet.getBalanceCached(), IdempotencyKey));

            return "Transaction created successfully";
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Possible duplicate transaction for idempotency key");
        }
    }

    @Override
    @Transactional
    public String transferFunds(UUID fromWalletId, UUID toWalletId, BigInteger amount, String idempotencyKey) {

        if (fromWalletId.equals(toWalletId)) {
            throw new RuntimeException("You can not transfer to your wallet");
        }

        Wallet sender = walletRepository.selectByIdForUpdate(fromWalletId).orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet recipient = walletRepository.selectByIdForUpdate(toWalletId).orElseThrow(() -> new RuntimeException("Recipient wallet not found"));


        sender.updateBalanceCache(amount.negate());
        BigInteger balanceBeforeCharges = sender.getBalanceCached();

        BigInteger charges = rateService.getRate(amount, ChargeTypeEnum.TRANSFER_FEE);
        sender.updateBalanceCache(charges.negate());

        recipient.updateBalanceCache(amount);

        try {
            ledgerEntryRepository.save(new LedgerEntry(sender, amount, EntryTypeEnum.DEBIT, balanceBeforeCharges, this.createIdempotencyKeyRef(idempotencyKey, EntryTypeEnum.DEBIT)));

            ledgerEntryRepository.save(new LedgerEntry(sender, charges, EntryTypeEnum.CHARGE, sender.getBalanceCached(), this.createIdempotencyKeyRef(idempotencyKey, EntryTypeEnum.CHARGE)));

            ledgerEntryRepository.save(new LedgerEntry(recipient, amount, EntryTypeEnum.CREDIT, recipient.getBalanceCached(), this.createIdempotencyKeyRef(idempotencyKey, EntryTypeEnum.CREDIT)));

            return "Transfer done successfully";
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Possible duplicate transaction for idempotency key");
        }

    }

    private String createIdempotencyKeyRef(String key, EntryTypeEnum type) {
        return key.trim() + "::" + type.name();
    }
}
