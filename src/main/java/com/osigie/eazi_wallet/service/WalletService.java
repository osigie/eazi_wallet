package com.osigie.eazi_wallet.service;

import com.osigie.eazi_wallet.domain.EntryTypeEnum;
import com.osigie.eazi_wallet.domain.Wallet;

import java.math.BigInteger;
import java.util.UUID;

public interface WalletService {

    Wallet createWallet(Wallet wallet);

    Wallet getWallet(UUID id);

    String topUp(UUID fromWalletId, BigInteger amount, String idempotencyKey);

    String transferFunds(UUID fromWalletId, UUID toWalletId, BigInteger amount, String idempotencyKey);
}
