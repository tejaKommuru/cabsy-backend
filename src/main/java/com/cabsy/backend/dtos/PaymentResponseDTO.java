// src/main/java/com/cabsy/backend/dtos/PaymentResponseDTO.java
package com.cabsy.backend.dtos;

import com.cabsy.backend.models.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long rideId; // To link back to the ride
    private Double amount;
    private String paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentTime;
}
