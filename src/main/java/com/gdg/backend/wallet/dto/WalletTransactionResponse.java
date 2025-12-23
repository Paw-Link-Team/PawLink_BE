package com.gdg.backend.wallet.dto;

import com.gdg.backend.wallet.domain.TransactionType;
import com.gdg.backend.wallet.domain.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WalletTransactionResponse {

    private Long id;
    private TransactionType type;
    private long amount;
    private long balanceAfter;
    private String reason;
    private LocalDateTime createdAt;

    public static WalletTransactionResponse from(WalletTransaction tx) {
        return WalletTransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .reason(tx.getReason())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
