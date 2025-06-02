// src/main/java/com/cabsy/backend/services/impl/UserServiceImpl.java
package com.cabsy.backend.services.impl;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder; // New import
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // For transactional operations
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if user with email or phone already exists
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists"); // TODO: Custom exception
        }
        if (userRepository.findByPhoneNumber(registrationDTO.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("User with this phone number already exists"); // TODO: Custom exception
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        // HASH THE PASSWORD BEFORE SAVING
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getPhoneNumber(), savedUser.getRating());
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRating()));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRating()))
                .collect(Collectors.toList());
    }
}