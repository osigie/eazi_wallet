package com.osigie.eazi_wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osigie.eazi_wallet.config.AbstractContainerBaseTest;
import com.osigie.eazi_wallet.domain.Wallet;
import com.osigie.eazi_wallet.dto.request.TransactionRequestDto;
import com.osigie.eazi_wallet.dto.request.TransferRequest;
import com.osigie.eazi_wallet.dto.response.BaseResponseDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionControllerE2ETests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    private Wallet senderWallet;
    private Wallet recipientWallet;
    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @BeforeEach
    public void setup() {
        ledgerEntryRepository.deleteAll();
        walletRepository.deleteAll();

        senderWallet = Wallet.builder()
                .currencyCode("NGN")
                .balanceCached(BigInteger.valueOf(100))
                .build();
        senderWallet = walletRepository.save(senderWallet);

        recipientWallet = Wallet.builder()
                .currencyCode("NGN")
                .balanceCached(BigInteger.valueOf(50))
                .build();
        recipientWallet = walletRepository.save(recipientWallet);
    }

    @Test
    public void topUp_withValidRequest_shouldReturnSuccess() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto(
                senderWallet.getId(),
                BigInteger.valueOf(20),
                "unique-topup-key"
        );

        String responseJson = mockMvc.perform(post("/transactions/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BaseResponseDto<String> response = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructParametricType(BaseResponseDto.class, String.class));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.data()).isEqualTo("Transaction created successfully");

        Wallet updatedWallet = walletRepository.findById(senderWallet.getId()).orElseThrow();
        assertThat(updatedWallet.getBalanceCached()).isEqualByComparingTo(BigInteger.valueOf(120));
    }

    @Test
    public void transfer_withValidRequest_shouldReturnSuccess() throws Exception {
        TransferRequest transferRequest = new TransferRequest(
                senderWallet.getId(),
                recipientWallet.getId(),
                BigInteger.valueOf(30),
                "unique-transfer-key"
        );

        String responseJson = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BaseResponseDto<String> response = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructParametricType(BaseResponseDto.class, String.class));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.data()).isEqualTo("Transfer completed successfully");

        Wallet updatedSender = walletRepository.findById(senderWallet.getId()).orElseThrow();
        Wallet updatedRecipient = walletRepository.findById(recipientWallet.getId()).orElseThrow();

        assertThat(updatedSender.getBalanceCached()).isLessThan(BigInteger.valueOf(100));
        assertThat(updatedRecipient.getBalanceCached()).isGreaterThan(BigInteger.valueOf(50));
    }
}
