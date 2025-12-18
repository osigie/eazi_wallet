package com.osigie.eazi_wallet.dto.response;

public record BaseResponseDto<T>(String message, int statusCode, T data) {
}

