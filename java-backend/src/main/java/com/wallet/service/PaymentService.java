package com.wallet.service;

import com.wallet.dto.AddMoneyRequest;
import com.wallet.dto.TransactionResponse;
import com.wallet.dto.TransferRequest;
import com.wallet.entity.LedgerEntry;
import com.wallet.entity.Transaction;
import com.wallet.entity.Wallet;
import com.wallet.exception.DuplicateRequestException;
import com.wallet.exception.InsufficientBalanceException;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.exception.WalletException;
import com.wallet.repository.LedgerRepository;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;
    private final IdempotencyService idempotencyService;
    private final AuditService auditService;
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse addMoney(AddMoneyRequest request, Long userId) {
        
        if (idempotencyService.isProcessed(request.getIdempotencyKey())) {
            log.info("Duplicate request detected: {}", request.getIdempotencyKey());
            Object cachedResponse = idempotencyService.getResponse(request.getIdempotencyKey());
            if (cachedResponse instanceof TransactionResponse) {
                return (TransactionResponse) cachedResponse;
            }
            throw new DuplicateRequestException("Request already processed");
        }
        
        Wallet wallet = walletRepository.findByIdWithLock(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new WalletException("Access denied");
        }
        
        if (wallet.getStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new WalletException("Wallet is not active");
        }
        
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletException("Amount must be greater than zero");
        }
        
        String referenceId = UUID.randomUUID().toString();
        
        Transaction transaction = Transaction.builder()
                .referenceId(referenceId)
                .fromWalletId(0L)
                .toWalletId(wallet.getId())
                .amount(request.getAmount())
                .type(Transaction.TransactionType.ADD_MONEY)
                .status(Transaction.TransactionStatus.PENDING)
                .description(request.getDescription())
                .build();
        
        try {
            BigDecimal oldBalance = wallet.getBalance();
            BigDecimal newBalance = oldBalance.add(request.getAmount());
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
            
            transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            transaction = transactionRepository.save(transaction);
            
            LedgerEntry ledgerEntry = LedgerEntry.builder()
                    .transactionId(transaction.getId())
                    .walletId(wallet.getId())
                    .entryType(LedgerEntry.EntryType.CREDIT)
                    .amount(request.getAmount())
                    .balanceAfter(newBalance)
                    .description("Money added: " + request.getDescription())
                    .build();
            ledgerRepository.save(ledgerEntry);
            
            auditService.log(userId, "ADD_MONEY", "TRANSACTION", transaction.getId(), 
                    "Added " + request.getAmount() + " to wallet " + wallet.getId());
            
            TransactionResponse response = toTransactionResponse(transaction);
            idempotencyService.markAsProcessed(request.getIdempotencyKey(), response);
            
            log.info("Money added successfully. Transaction: {}", referenceId);
            return response;
            
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            throw new WalletException("Failed to add money: " + e.getMessage(), e);
        }
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse transfer(TransferRequest request, Long userId) {
        
        if (idempotencyService.isProcessed(request.getIdempotencyKey())) {
            log.info("Duplicate request detected: {}", request.getIdempotencyKey());
            Object cachedResponse = idempotencyService.getResponse(request.getIdempotencyKey());
            if (cachedResponse instanceof TransactionResponse) {
                return (TransactionResponse) cachedResponse;
            }
            throw new DuplicateRequestException("Request already processed");
        }
        
        if (request.getFromWalletId().equals(request.getToWalletId())) {
            throw new WalletException("Cannot transfer to same wallet");
        }
        
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletException("Amount must be greater than zero");
        }
        
        Wallet fromWallet = walletRepository.findByIdWithLock(request.getFromWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found"));
        
        if (!fromWallet.getUser().getId().equals(userId)) {
            throw new WalletException("Access denied to source wallet");
        }
        
        Wallet toWallet = walletRepository.findByIdWithLock(request.getToWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));
        
        if (fromWallet.getStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new WalletException("Source wallet is not active");
        }
        
        if (toWallet.getStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new WalletException("Destination wallet is not active");
        }
        
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source wallet");
        }
        
        String referenceId = UUID.randomUUID().toString();
        
        Transaction transaction = Transaction.builder()
                .referenceId(referenceId)
                .fromWalletId(fromWallet.getId())
                .toWalletId(toWallet.getId())
                .amount(request.getAmount())
                .type(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.PENDING)
                .description(request.getDescription())
                .build();
        
        try {
            BigDecimal fromOldBalance = fromWallet.getBalance();
            BigDecimal fromNewBalance = fromOldBalance.subtract(request.getAmount());
            fromWallet.setBalance(fromNewBalance);
            walletRepository.save(fromWallet);
            
            BigDecimal toOldBalance = toWallet.getBalance();
            BigDecimal toNewBalance = toOldBalance.add(request.getAmount());
            toWallet.setBalance(toNewBalance);
            walletRepository.save(toWallet);
            
            transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
            transaction = transactionRepository.save(transaction);
            
            LedgerEntry debitEntry = LedgerEntry.builder()
                    .transactionId(transaction.getId())
                    .walletId(fromWallet.getId())
                    .entryType(LedgerEntry.EntryType.DEBIT)
                    .amount(request.getAmount())
                    .balanceAfter(fromNewBalance)
                    .description("Transfer to wallet " + toWallet.getId())
                    .build();
            ledgerRepository.save(debitEntry);
            
            LedgerEntry creditEntry = LedgerEntry.builder()
                    .transactionId(transaction.getId())
                    .walletId(toWallet.getId())
                    .entryType(LedgerEntry.EntryType.CREDIT)
                    .amount(request.getAmount())
                    .balanceAfter(toNewBalance)
                    .description("Transfer from wallet " + fromWallet.getId())
                    .build();
            ledgerRepository.save(creditEntry);
            
            auditService.log(userId, "TRANSFER", "TRANSACTION", transaction.getId(),
                    "Transferred " + request.getAmount() + " from wallet " + fromWallet.getId() + 
                    " to wallet " + toWallet.getId());
            
            TransactionResponse response = toTransactionResponse(transaction);
            idempotencyService.markAsProcessed(request.getIdempotencyKey(), response);
            
            log.info("Transfer successful. Transaction: {}", referenceId);
            return response;
            
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            throw new WalletException("Transfer failed: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionHistory(Long walletId, Long userId, Pageable pageable) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new WalletException("Access denied");
        }
        
        Page<Transaction> transactions = transactionRepository.findByWalletId(walletId, pageable);
        return transactions.map(this::toTransactionResponse);
    }
    
    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .referenceId(transaction.getReferenceId())
                .fromWalletId(transaction.getFromWalletId())
                .toWalletId(transaction.getToWalletId())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .description(transaction.getDescription())
                .failureReason(transaction.getFailureReason())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}