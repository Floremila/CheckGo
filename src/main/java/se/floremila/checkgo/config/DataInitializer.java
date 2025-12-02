package se.floremila.checkgo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.floremila.checkgo.entity.Role;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.repository.RoleRepository;
import se.floremila.checkgo.repository.UserRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role userRole = createRoleIfNotExists("ROLE_USER");
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");

        createAdminUserIfNotExists(adminRole);
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(roleName).build()
                ));
    }

    private void createAdminUserIfNotExists(Role adminRole) {
        String adminUsername = "admin";

        if (userRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .email("admin@checkgo.local")
                .password(passwordEncoder.encode("admin123"))
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
    }
}


