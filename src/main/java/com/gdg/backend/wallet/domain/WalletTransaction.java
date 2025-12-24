package com.gdg.backend.wallet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private long balanceAfter;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public WalletTransaction(
            Long userId,
            TransactionType type,
            long amount,
            long balanceAfter,
            String reason
    ) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public static WalletTransaction charge(
            Long userId,
            long amount,
            long balanceAfter
    ) {
        return WalletTransaction.builder()
                .userId(userId)
                .type(TransactionType.CHARGE)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .reason("충전")
                .build();
    }

    public static WalletTransaction use(
            Long userId,
            long amount,
            long balanceAfter,
            String reason
    ) {
        return WalletTransaction.builder()
                .userId(userId)
                .type(TransactionType.USE)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .reason(reason)
                .build();
    }
}
