package com.osigie.eazi_wallet.dto.request;

import com.osigie.eazi_wallet.domain.EntryTypeEnum;

import java.math.BigInteger;
import java.util.UUID;

public record TransactionRequestDto(UUID fromWalletId, BigInteger amount, EntryTypeEnum type, String idempotencyKey) {
}
