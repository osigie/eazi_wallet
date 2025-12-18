package com.osigie.eazi_wallet.dto.response;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WalletResponseDto(UUID id, BigInteger balance, String currencyCode, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
