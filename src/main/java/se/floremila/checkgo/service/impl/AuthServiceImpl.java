package se.floremila.checkgo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.floremila.checkgo.dto.AuthResponse;
import se.floremila.checkgo.dto.LoginRequest;
import se.floremila.checkgo.dto.RegisterRequest;
import se.floremila.checkgo.entity.Role;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.repository.RoleRepository;
import se.floremila.checkgo.repository.UserRepository;
import se.floremila.checkgo.service.AuthService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }


        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));


        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);


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
        throw new UnsupportedOperationException("Login not implemented yet");
    }
}
