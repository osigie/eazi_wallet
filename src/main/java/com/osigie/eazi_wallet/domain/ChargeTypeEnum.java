package com.osigie.eazi_wallet.domain;

public enum ChargeTypeEnum {
    TRANSFER_FEE(1, "Transfer Fee", "Fee for transferring funds to another wallet");

    private final int code;
    private final String displayName;
    private final String description;

    ChargeTypeEnum(int code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }
}
