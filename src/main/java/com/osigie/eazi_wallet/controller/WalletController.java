package com.osigie.eazi_wallet.controller;

import com.osigie.eazi_wallet.domain.Wallet;
import com.osigie.eazi_wallet.dto.request.CreateWalletRequestDto;
import com.osigie.eazi_wallet.dto.response.BaseResponseDto;
import com.osigie.eazi_wallet.dto.response.WalletResponseDto;
import com.osigie.eazi_wallet.mapper.WalletMapper;
import com.osigie.eazi_wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;


    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }


    @PostMapping
    public ResponseEntity<BaseResponseDto<WalletResponseDto>> create(@RequestBody CreateWalletRequestDto dto) {
        Wallet wallet = walletService.createWallet(walletMapper.mapEntity(dto));
        return new ResponseEntity<>(new BaseResponseDto<>("success", HttpStatus.CREATED.value(), walletMapper.mapDto(wallet)), HttpStatus.CREATED);

    }
}
