package com.osigie.eazi_wallet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigInteger;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "fromWalletId is required")
        UUID fromWalletId,

        @NotNull(message = "toWalletId is required")
        UUID toWalletId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        BigInteger amount,

        @NotBlank(message = "idempotencyKey is required")
        @Size(max = 100, message = "idempotencyKey must not exceed 100 characters")
        String idempotencyKey) {
}
