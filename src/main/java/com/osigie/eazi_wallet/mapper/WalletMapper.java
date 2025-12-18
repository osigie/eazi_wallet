package com.osigie.eazi_wallet.mapper;

import com.osigie.eazi_wallet.domain.Wallet;
import com.osigie.eazi_wallet.dto.request.CreateWalletRequestDto;
import com.osigie.eazi_wallet.dto.response.WalletResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapper {

    @Mapping(target = "balance", source = "balanceCached")
    WalletResponseDto mapDto(Wallet source);

    @Mapping(target = "currencyCode", source = "currencyCode")
    Wallet mapEntity(CreateWalletRequestDto source);
}
