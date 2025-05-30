// src/main/java/com/cabsy/backend/models/Payment.java
package com.cabsy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments") // Maps to the 'payments' table in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // One payment per ride
    @JoinColumn(name = "ride_id", nullable = false, unique = true) // ride_id is unique
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who made the payment

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String method; // e.g., "Credit Card", "UPI", "Cash"

    @Column(name = "transaction_id", unique = true, length = 255)
    private String transactionId; // From payment gateway

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // e.g., PENDING, PAID, FAILED

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING; // Default status
        }
    }
}