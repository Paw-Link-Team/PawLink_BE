package com.gdg.backend.wallet.service;

import com.gdg.backend.wallet.domain.TransactionType;
import com.gdg.backend.wallet.domain.Wallet;
import com.gdg.backend.wallet.domain.WalletTransaction;
import com.gdg.backend.wallet.dto.WalletTransactionResponse;
import com.gdg.backend.wallet.repository.WalletRepository;
import com.gdg.backend.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public void earn(Long userId, long amount, String reason) {

        Wallet wallet = getOrCreateWallet(userId);

        wallet.earn(amount);

        walletTransactionRepository.save(
                new WalletTransaction(
                        userId,
                        TransactionType.EARN,
                        amount,
                        wallet.getBalance(),
                        reason
                )
        );
    }

    @Transactional
    public void use(Long userId, long amount, String reason) {

        Wallet wallet = getOrCreateWallet(userId);

        wallet.use(amount);

        walletTransactionRepository.save(
                new WalletTransaction(
                        userId,
                        TransactionType.USE,
                        -amount,
                        wallet.getBalance(),
                        reason
                )
        );
    }

    @Transactional
    public List<WalletTransactionResponse> getMyTransactions(Long userId) {

        return walletTransactionRepository
                .findTop50ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(WalletTransactionResponse::from)
                .toList();
    }

    @Transactional
    public long getBalance(Long userId) {
        return walletRepository.findById(userId)
                .map(Wallet::getBalance)
                .orElse(0L);
    }

    @Transactional
    public void charge(Long userId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        Wallet wallet = getOrCreateWallet(userId);

        wallet.increase(amount);

        WalletTransaction transaction = WalletTransaction.charge(
                userId,
                amount,
                wallet.getBalance()
        );


        walletTransactionRepository.save(transaction);
    }

    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findById(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }
}

