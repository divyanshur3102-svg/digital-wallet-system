package com.wallet.controller;

import com.wallet.dto.ApiResponse;
import com.wallet.dto.WalletResponse;
import com.wallet.security.JwtTokenProvider;
import com.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Wallet", description = "Wallet Management APIs")
public class WalletController {
    
    private final WalletService walletService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/create")
    @Operation(summary = "Create new wallet")
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false, defaultValue = "USD") String currency) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        WalletResponse wallet = walletService.createWallet(userId, currency);
        return ResponseEntity.ok(ApiResponse.success("Wallet created successfully", wallet));
    }
    
    @GetMapping("/{walletId}")
    @Operation(summary = "Get wallet details")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(
            @RequestHeader("Authorization") String token,
            @PathVariable Long walletId) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        WalletResponse wallet = walletService.getWallet(walletId, userId);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }
    
    @GetMapping("/my-wallets")
    @Operation(summary = "Get all wallets of logged in user")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getMyWallets(
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        List<WalletResponse> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(ApiResponse.success(wallets));
    }
    
    @GetMapping("/{walletId}/balance")
    @Operation(summary = "Get wallet balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(
            @RequestHeader("Authorization") String token,
            @PathVariable Long walletId) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        BigDecimal balance = walletService.getBalance(walletId, userId);
        return ResponseEntity.ok(ApiResponse.success("Current balance", balance));
    }
}