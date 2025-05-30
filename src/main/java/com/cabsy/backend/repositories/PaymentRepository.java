// src/main/java/com/cabsy/backend/repositories/PaymentRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.Payment;
import com.cabsy.backend.models.PaymentStatus; // Import the enum
import com.cabsy.backend.models.Ride;
import com.cabsy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find a payment by its associated ride
    Optional<Payment> findByRide(Ride ride);

    // Find payments made by a specific user
    List<Payment> findByUser(User user);

    // Find payments by their status
    List<Payment> findByStatus(PaymentStatus status);

    // Find payments by user and status
    List<Payment> findByUserAndStatus(User user, PaymentStatus status);

    // Find a payment by its transaction ID
    Optional<Payment> findByTransactionId(String transactionId);
}
