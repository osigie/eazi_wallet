package com.osigie.eazi_wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osigie.eazi_wallet.config.AbstractContainerBaseTest;
import com.osigie.eazi_wallet.domain.Wallet;
import com.osigie.eazi_wallet.dto.request.CreateWalletRequestDto;
import com.osigie.eazi_wallet.dto.response.BaseResponseDto;
import com.osigie.eazi_wallet.dto.response.WalletResponseDto;
import com.osigie.eazi_wallet.repository.LedgerEntryRepository;
import com.osigie.eazi_wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WalletControllerE2ETests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @BeforeEach
    public void setup() {
        ledgerEntryRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    public void createWallet_thenReturnCreatedWallet() throws Exception {
        CreateWalletRequestDto createDto = new CreateWalletRequestDto("NGN");

        String responseJson = mockMvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.currencyCode").value("NGN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BaseResponseDto<WalletResponseDto> response = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructParametricType(BaseResponseDto.class, WalletResponseDto.class));

        assertThat(response.message()).isEqualTo("success");
        assertThat(response.data()).isNotNull();
        assertThat(response.data().currencyCode()).isEqualTo("NGN");
        assertThat(response.data().id()).isNotNull();
    }

    @Test
    public void getWalletById_thenReturnWallet() throws Exception {

        Wallet wallet = Wallet.builder()
                .currencyCode("USD")
                .balanceCached(BigInteger.TEN)
                .build();

        wallet = walletRepository.save(wallet);

        mockMvc.perform(get("/wallets/{id}", wallet.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(wallet.getId().toString()))
                .andExpect(jsonPath("$.data.currencyCode").value("USD"));
    }
}
