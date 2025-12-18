package com.osigie.eazi_wallet.dto.request;

import java.math.BigInteger;
import java.util.UUID;

public record TransferRequest(UUID fromWalletId, UUID toWalletId, BigInteger amount,  String idempotencyKey) {
}
