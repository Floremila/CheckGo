package se.floremila.checkgo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.floremila.checkgo.config.RabbitMQConfig;
import se.floremila.checkgo.dto.AuthResponse;
import se.floremila.checkgo.dto.LoginRequest;
import se.floremila.checkgo.dto.RegisterRequest;
import se.floremila.checkgo.entity.Role;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.exception.BadRequestException;
import se.floremila.checkgo.exception.NotFoundException;
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
    private final RabbitTemplate rabbitTemplate;

    @Override
    public AuthResponse register(RegisterRequest request) {

        log.info("Register attempt for username '{}'", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username '{}' already exists", request.getUsername());
            throw new BadRequestException("Username already in use");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email '{}' already exists", request.getEmail());
            throw new BadRequestException("Email already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("Default role ROLE_USER not found"));


        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        log.info("User '{}' created with id {} (enabled = false)", user.getUsername(), user.getId());


        rabbitTemplate.convertAndSend(RabbitMQConfig.ACTIVATION_QUEUE, user.getId());
        log.info("Activation message sent to RabbitMQ for userId {}", user.getId());

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

        log.info("Login attempt for username '{}'", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        String token = jwtService.generateToken(userDetails);

        log.info("Login successful for user '{}' (id: {})", user.getUsername(), user.getId());

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

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


