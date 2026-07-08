package com.sigepid.auth.application.service;

import com.sigepid.auth.application.dto.AuthResponse;
import com.sigepid.auth.application.dto.LoginRequest;
import com.sigepid.auth.application.dto.RegisterRequest;
import com.sigepid.auth.domain.entity.User;
import com.sigepid.auth.domain.enums.Role;
import com.sigepid.auth.domain.repository.UserRepository;
import com.sigepid.auth.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    //metodo para registrar un nuevo usuario, se valida que el username y email no existan, se encripta la contraseña y se genera un token JWT
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Enforce USER role for new registrations
                .preferredCategories(request.getPreferredCategories() != null ? request.getPreferredCategories() : new java.util.ArrayList<>())
                .ageRange(request.getAgeRange())
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtProvider.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .userId(savedUser.getId())
                .build();
    }

    //metodo para login de usuario, se autentica y genera un token JWT
    public AuthResponse login(LoginRequest request) {
//se autentica el usuario con el username y password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
//se obtiene el usuario de la base de datos y se genera un token JWT
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));
//se genera el token JWT
        String token = jwtProvider.generateToken(user);
//se retorna la respuesta con el token, username y role del usuario
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .userId(user.getId())
                .build();
    }

    public com.sigepid.auth.application.dto.AuthProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return com.sigepid.auth.application.dto.AuthProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .preferredCategories(user.getPreferredCategories())
                .ageRange(user.getAgeRange())
                .build();
    }

    @Transactional
    public com.sigepid.auth.application.dto.AuthProfileResponse updateEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
            throw new RuntimeException("Email is already in use: " + newEmail);
        }

        user.setEmail(newEmail);
        User updatedUser = userRepository.save(user);

        return com.sigepid.auth.application.dto.AuthProfileResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .preferredCategories(updatedUser.getPreferredCategories())
                .ageRange(updatedUser.getAgeRange())
                .build();
    }

    public com.sigepid.auth.application.dto.UserPreferencesResponse getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return com.sigepid.auth.application.dto.UserPreferencesResponse.builder()
                .userId(user.getId())
                .preferredCategories(user.getPreferredCategories())
                .ageRange(user.getAgeRange())
                .build();
    }

    @Transactional
    public com.sigepid.auth.application.dto.AuthProfileResponse updatePreferences(Long userId, com.sigepid.auth.application.dto.UserPreferencesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setPreferredCategories(request.getPreferredCategories());
        user.setAgeRange(request.getAgeRange());
        User updatedUser = userRepository.save(user);

        return com.sigepid.auth.application.dto.AuthProfileResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .preferredCategories(updatedUser.getPreferredCategories())
                .ageRange(updatedUser.getAgeRange())
                .build();
    }
}
