package se.floremila.checkgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.floremila.checkgo.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwnerId(Long ownerId);
}

