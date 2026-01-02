package se.floremila.checkgo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.floremila.checkgo.dto.AuthResponse;
import se.floremila.checkgo.dto.LoginRequest;
import se.floremila.checkgo.dto.RegisterRequest;
import se.floremila.checkgo.entity.Role;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.messaging.UserActivationPublisher;
import se.floremila.checkgo.repository.RoleRepository;
import se.floremila.checkgo.repository.UserRepository;
import se.floremila.checkgo.security.JwtService;
import se.floremila.checkgo.service.AuthService;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserActivationPublisher userActivationPublisher; // 👈 nuevo

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Register attempt for username={}, email={}", request.getUsername(), request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Register failed: username {} already in use", request.getUsername());
            throw new RuntimeException("Username already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Register failed: email {} already in use", request.getEmail());
            throw new RuntimeException("Email already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("Default role ROLE_USER not found in database");
                    return new RuntimeException("Default role ROLE_USER not found");
                });

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        log.info("User registered with id={}, username={}", user.getId(), user.getUsername());


        try {
            userActivationPublisher.sendActivationRequest(user.getId());
            log.info("Activation request sent for user id={}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send activation request for user {}", user.getId(), e);
        }

        return AuthResponse.builder()
                .accessToken(null)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(Set.of(userRole.getName()))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username={}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: user {} not found after authentication", request.getUsername());
                    return new RuntimeException("User not found");
                });

        String token = jwtService.generateToken(userDetails);

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        log.info("Login successful for user id={}, username={}", user.getId(), user.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }
}



