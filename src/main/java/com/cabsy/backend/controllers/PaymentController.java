// src/main/java/com/cabsy/backend/controllers/PaymentController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.PaymentRequestDTO;
import com.cabsy.backend.dtos.PaymentResponseDTO; // Import the PaymentResponseDTO
import com.cabsy.backend.models.Payment;
import com.cabsy.backend.models.PaymentStatus;
import com.cabsy.backend.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments") // Base path for payment related endpoints
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Endpoint to create a new payment record for a completed ride.
     * This assumes the payment is being recorded after a ride has finished
     * and the amount is known.
     *
     * @param paymentRequestDTO DTO containing rideId, amount, and paymentMethod.
     * @return ApiResponse indicating success or failure of payment creation, with PaymentResponseDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            // When a payment is initiated from the frontend upon ride completion,
            // we create a payment record.
            Payment newPayment = paymentService.createPayment(
                    paymentRequestDTO.getRideId(),
                    paymentRequestDTO.getAmount(),
                    paymentRequestDTO.getPaymentMethod()
            );

            // Mark the payment as COMPLETED immediately since the frontend indicates it's done.
            // A dummy transaction ID is used for now; integrate with a real payment gateway for actual IDs.
            newPayment = paymentService.updatePaymentStatus(newPayment.getId(), PaymentStatus.COMPLETED, "FRONTEND_INITIATED_TRANSACTION");

            // Map the created Payment entity to PaymentResponseDTO for the response.
            // This prevents lazy loading issues by explicitly selecting the data to be returned.
            PaymentResponseDTO responseDTO = new PaymentResponseDTO(
                    newPayment.getId(),
                    newPayment.getRide().getId(), // Accessing ride ID. Ensure the ride object is accessible here (eagerly fetched or session is still open).
                    newPayment.getAmount(),
                    newPayment.getPaymentMethod(),
                    newPayment.getStatus(),
                    newPayment.getTransactionId(),
                    newPayment.getPaymentTime()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded successfully", responseDTO));
        } catch (RuntimeException e) {
            // Catch custom exceptions for more specific error handling
            System.err.println("Error creating payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Payment failed", e.getMessage()));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during payment creation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Payment failed", "An internal server error occurred."));
        }
    }
}
