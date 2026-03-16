package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String referenceId;
    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private String failureReason;
    private LocalDateTime createdAt;
}