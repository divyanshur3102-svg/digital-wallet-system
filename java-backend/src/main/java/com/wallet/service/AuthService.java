package com.wallet.service;

import com.wallet.dto.LoginRequest;
import com.wallet.dto.RegisterRequest;
import com.wallet.dto.AuthResponse;
import com.wallet.entity.User;
import com.wallet.exception.WalletException;
import com.wallet.repository.UserRepository;
import com.wallet.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new WalletException("Email already registered");
        }
        
        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.ROLE_USER);
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .status(User.UserStatus.ACTIVE)
                .roles(roles)
                .build();
        
        user = userRepository.save(user);
        auditService.log(user.getId(), "REGISTER", "USER", user.getId(), "User registered");
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
        
        String token = jwtTokenProvider.generateToken(userDetails, user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new WalletException("User not found"));
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new WalletException("Account is " + user.getStatus());
        }
        
        auditService.log(user.getId(), "LOGIN", "USER", user.getId(), "User logged in");
        
        String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal(), user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}