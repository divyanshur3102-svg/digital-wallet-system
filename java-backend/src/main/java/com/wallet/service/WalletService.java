package com.wallet.service;

import com.wallet.dto.TransactionResponse;
import com.wallet.dto.WalletResponse;
import com.wallet.entity.User;
import com.wallet.entity.Wallet;
import com.wallet.exception.ResourceNotFoundException;
import com.wallet.exception.WalletException;
import com.wallet.repository.UserRepository;
import com.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    
    @Transactional
    public WalletResponse createWallet(Long userId, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .currency(currency != null ? currency : "USD")
                .status(Wallet.WalletStatus.ACTIVE)
                .build();
        
        wallet = walletRepository.save(wallet);
        auditService.log(userId, "CREATE_WALLET", "WALLET", wallet.getId(), "Wallet created");
        
        return toWalletResponse(wallet);
    }
    
    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new WalletException("Access denied");
        }
        
        return toWalletResponse(wallet);
    }
    
    @Transactional(readOnly = true)
    public List<WalletResponse> getUserWallets(Long userId) {
        List<Wallet> wallets = walletRepository.findAllByUserId(userId);
        return wallets.stream()
                .map(this::toWalletResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new WalletException("Access denied");
        }
        
        return wallet.getBalance();
    }
    
    private WalletResponse toWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus().name())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}