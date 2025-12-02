package se.floremila.checkgo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return ResponseEntity.ok(
                new UserMeResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                )
        );
    }

    private record UserMeResponse(Long id, String username, String email) {}
}

