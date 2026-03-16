package com.wallet.repository;

import com.wallet.entity.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {
    
    List<LedgerEntry> findByTransactionId(Long transactionId);
    
    Page<LedgerEntry> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);
    
    List<LedgerEntry> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}