// src/main/java/com/cabsy/backend/controllers/UserController.java
package com.cabsy.backend.controllers;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userDTO -> ResponseEntity.ok(ApiResponse.success("User fetched successfully", userDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found", "User with ID " + id + " does not exist")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

    // TODO: Add PUT mapping for updating user profile
    // TODO: Add DELETE mapping for deleting user (requires careful consideration)
}