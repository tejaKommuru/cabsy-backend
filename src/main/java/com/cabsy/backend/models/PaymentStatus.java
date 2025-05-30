// src/main/java/com/cabsy/backend/models/PaymentStatus.java
package com.cabsy.backend.models;

public enum PaymentStatus {
    PENDING, // Payment initiated but not confirmed
    PAID, // Payment successful
    FAILED, // Payment transaction failed
    REFUNDED, // Payment has been refunded
    CASH // Payment method is cash (handled differently)
}