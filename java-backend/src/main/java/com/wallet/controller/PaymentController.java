package com.wallet.controller;

import com.wallet.dto.AddMoneyRequest;
import com.wallet.dto.ApiResponse;
import com.wallet.dto.TransactionResponse;
import com.wallet.dto.TransferRequest;
import com.wallet.security.JwtTokenProvider;
import com.wallet.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Payment", description = "Payment APIs")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/add-money")
    @Operation(summary = "Add money to wallet")
    public ResponseEntity<ApiResponse<TransactionResponse>> addMoney(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AddMoneyRequest request) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        TransactionResponse transaction = paymentService.addMoney(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Money added successfully", transaction));
    }
    
    @PostMapping("/transfer")
    @Operation(summary = "Transfer money between wallets")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TransferRequest request) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        TransactionResponse transaction = paymentService.transfer(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", transaction));
    }
    
    @GetMapping("/history/{walletId}")
    @Operation(summary = "Get transaction history")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long userId = jwtTokenProvider.extractUserId(token.substring(7));
        Page<TransactionResponse> history = paymentService.getTransactionHistory(
                walletId, userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}