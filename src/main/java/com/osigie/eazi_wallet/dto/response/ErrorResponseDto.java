package com.osigie.eazi_wallet.dto.response;

import org.springframework.http.HttpStatus;

public record ErrorResponseDto(HttpStatus status, String message, int statusCode) {
}
