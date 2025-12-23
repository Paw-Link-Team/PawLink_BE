package com.gdg.backend.wallet.repository;

import com.gdg.backend.wallet.domain.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
}
