// src/main/java/com/cabsy/backend/repositories/UserRepository.java
package com.cabsy.backend.repositories;

import com.cabsy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email (useful for login)
    Optional<User> findByEmail(String email);

    // Find a user by their phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Check if a user with a given email already exists
    boolean existsByEmail(String email);

    // Check if a user with a given phone number already exists
    boolean existsByPhoneNumber(String phoneNumber);
}