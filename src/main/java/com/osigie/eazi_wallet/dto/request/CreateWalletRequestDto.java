package com.osigie.eazi_wallet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateWalletRequestDto(
        @NotBlank(message = "currency code is required")
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a valid ISO 4217 code")
        String currencyCode) {
}
