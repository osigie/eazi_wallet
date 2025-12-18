package com.osigie.eazi_wallet.controller;

import com.osigie.eazi_wallet.dto.request.TransactionRequestDto;
import com.osigie.eazi_wallet.dto.request.TransferRequest;
import com.osigie.eazi_wallet.dto.response.BaseResponseDto;
import com.osigie.eazi_wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    private final WalletService walletService;


    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }


    @PostMapping("/top-up")
    public ResponseEntity<BaseResponseDto<String>> topUp(@Valid @RequestBody TransactionRequestDto dto) {
        String response = walletService.topUp(
                dto.fromWalletId(),
                dto.amount(),
                dto.idempotencyKey()
        );
        return new ResponseEntity<>(new BaseResponseDto<>("success", HttpStatus.OK.value(), response), HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<BaseResponseDto<String>> transfer(@Valid @RequestBody TransferRequest dto) {
        String response = walletService.transferFunds(
                dto.fromWalletId(),
                dto.toWalletId(),
                dto.amount(),
                dto.idempotencyKey()
        );
        return new ResponseEntity<>(new BaseResponseDto<>("success", HttpStatus.OK.value(), response), HttpStatus.OK);
    }
}
