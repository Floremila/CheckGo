package se.floremila.checkgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.floremila.checkgo.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}

