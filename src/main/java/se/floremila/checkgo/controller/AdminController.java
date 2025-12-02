package se.floremila.checkgo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        List<UserSummary> users = userRepository.findAll().stream()
                .map(user -> new UserSummary(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    private record UserSummary(Long id, String username, String email) {}
}

