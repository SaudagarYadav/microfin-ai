package com.microfin.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microfin.auth.constants.AuthConstants;
import com.microfin.auth.dto.UserResponse;
import com.microfin.auth.repository.UserRepository;
import com.microfin.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * Admin-only management endpoints, secured via role-based authorization.
 */
@RestController
@RequestMapping(AuthConstants.ADMIN_BASE)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers() {
        final List<UserResponse> users = userRepository.findAll().stream()
                .map(authService::toUserResponse)
                .toList();
        return ResponseEntity.ok(users);
    }
}
