package com.osigie.eazi_wallet.service;

import com.osigie.eazi_wallet.domain.EntryTypeEnum;
import com.osigie.eazi_wallet.domain.Wallet;

import java.math.BigInteger;
import java.util.UUID;

public interface WalletService {

    Wallet createWallet(Wallet wallet);

    String createTransaction(UUID fromWalletId, BigInteger amount, EntryTypeEnum type, String IdempotencyKey);

    String transferFunds(UUID fromWalletId, UUID toWalletId, BigInteger amount, String idempotencyKey);
}
