package com.wallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_reference_id", columnList = "referenceId", unique = true),
    @Index(name = "idx_from_wallet", columnList = "fromWalletId"),
    @Index(name = "idx_to_wallet", columnList = "toWalletId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String referenceId;
    
    @Column(nullable = false)
    private Long fromWalletId;
    
    @Column(nullable = false)
    private Long toWalletId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 255)
    private String failureReason;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum TransactionType {
        ADD_MONEY, TRANSFER, WITHDRAWAL
    }
    
    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED, REVERSED
    }
}