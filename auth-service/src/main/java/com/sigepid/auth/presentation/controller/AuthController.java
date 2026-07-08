package com.sigepid.auth.presentation.controller;

import com.sigepid.auth.application.dto.AuthResponse;
import com.sigepid.auth.application.dto.LoginRequest;
import com.sigepid.auth.application.dto.RegisterRequest;
import com.sigepid.auth.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @org.springframework.web.bind.annotation.GetMapping("/profile")
    public ResponseEntity<com.sigepid.auth.application.dto.AuthProfileResponse> getProfile(
            @org.springframework.web.bind.annotation.RequestHeader("X-User-Id") String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        com.sigepid.auth.application.dto.AuthProfileResponse profile = authService.getProfile(Long.parseLong(userIdHeader));
        return ResponseEntity.ok(profile);
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile/email")
    public ResponseEntity<com.sigepid.auth.application.dto.AuthProfileResponse> updateEmail(
            @org.springframework.web.bind.annotation.RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody com.sigepid.auth.application.dto.UpdateEmailRequest request) {
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        com.sigepid.auth.application.dto.AuthProfileResponse updatedProfile = authService.updateEmail(Long.parseLong(userIdHeader), request.getNewEmail());
        return ResponseEntity.ok(updatedProfile);
    }

    @org.springframework.web.bind.annotation.GetMapping("/users/{userId}/preferences")
    public ResponseEntity<com.sigepid.auth.application.dto.UserPreferencesResponse> getUserPreferences(
            @org.springframework.web.bind.annotation.PathVariable Long userId) {
        com.sigepid.auth.application.dto.UserPreferencesResponse response = authService.getUserPreferences(userId);
        return ResponseEntity.ok(response);
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile/preferences")
    public ResponseEntity<com.sigepid.auth.application.dto.AuthProfileResponse> updatePreferences(
            @org.springframework.web.bind.annotation.RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody com.sigepid.auth.application.dto.UserPreferencesRequest request) {
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        com.sigepid.auth.application.dto.AuthProfileResponse updatedProfile = authService.updatePreferences(Long.parseLong(userIdHeader), request);
        return ResponseEntity.ok(updatedProfile);
    }
}
