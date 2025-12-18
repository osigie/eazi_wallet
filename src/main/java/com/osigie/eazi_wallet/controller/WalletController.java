package com.osigie.eazi_wallet.controller;

import com.osigie.eazi_wallet.domain.Wallet;
import com.osigie.eazi_wallet.dto.request.CreateWalletRequestDto;
import com.osigie.eazi_wallet.dto.response.BaseResponseDto;
import com.osigie.eazi_wallet.dto.response.WalletResponseDto;
import com.osigie.eazi_wallet.mapper.WalletMapper;
import com.osigie.eazi_wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<BaseResponseDto<WalletResponseDto>> create(@Valid @RequestBody CreateWalletRequestDto dto) {
        Wallet wallet = walletService.createWallet(walletMapper.mapEntity(dto));
        return new ResponseEntity<>(new BaseResponseDto<>("success", HttpStatus.CREATED.value(), walletMapper.mapDto(wallet)), HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDto<WalletResponseDto>> getWallet(@Valid @PathVariable UUID id) {
        Wallet wallet = walletService.getWallet(id);
        return new ResponseEntity<>(new BaseResponseDto<>("success", HttpStatus.OK.value(), walletMapper.mapDto(wallet)), HttpStatus.OK);
    }
}
