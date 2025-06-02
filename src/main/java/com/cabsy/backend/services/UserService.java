// src/main/java/com/cabsy/backend/services/UserService.java
package com.cabsy.backend.services;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    Optional<User> findUserByEmail(String email);
    Optional<UserResponseDTO> getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    // Add methods for updating profile, etc.
}