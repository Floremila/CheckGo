package se.floremila.checkgo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.floremila.checkgo.dto.TaskRequestDTO;
import se.floremila.checkgo.dto.TaskResponseDTO;
import se.floremila.checkgo.entity.Task;
import se.floremila.checkgo.entity.TaskStatus;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.exception.ForbiddenException;
import se.floremila.checkgo.exception.NotFoundException;
import se.floremila.checkgo.repository.TaskRepository;
import se.floremila.checkgo.repository.UserRepository;
import se.floremila.checkgo.service.TaskService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        log.debug("Fetching current user from SecurityContext: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<TaskResponseDTO> getMyTasks() {
        User user = getCurrentUser();
        log.info("Listing tasks for user id={}", user.getId());

        return taskRepository.findByOwnerId(user.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public TaskResponseDTO getMyTaskById(Long id) {
        User user = getCurrentUser();
        log.info("Getting task id={} for user id={}", id, user.getId());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (!task.getOwner().getId().equals(user.getId())) {
            log.warn("User id={} tried to access task id={} owned by user id={}",
                    user.getId(), id, task.getOwner().getId());
            throw new ForbiddenException("You are not allowed to access this task");
        }

        return toDTO(task);
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        User user = getCurrentUser();
        log.info("Creating task for user id={}", user.getId());

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.valueOf(request.getStatus()));
        if (request.getDueDate() != null) {
            task.setDueDate(LocalDate.parse(request.getDueDate()));
        }
        task.setOwner(user);

        taskRepository.save(task);
        log.debug("Task created with id={} for user id={}", task.getId(), user.getId());

        return toDTO(task);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        User user = getCurrentUser();
        log.info("Updating task id={} for user id={}", id, user.getId());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (!task.getOwner().getId().equals(user.getId())) {
            log.warn("User id={} tried to update task id={} owned by user id={}",
                    user.getId(), id, task.getOwner().getId());
            throw new ForbiddenException("You are not allowed to modify this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.valueOf(request.getStatus()));
        if (request.getDueDate() != null) {
            task.setDueDate(LocalDate.parse(request.getDueDate()));
        }

        taskRepository.save(task);
        log.debug("Task id={} updated by user id={}", id, user.getId());

        return toDTO(task);
    }

    @Override
    public void deleteTask(Long id) {
        User user = getCurrentUser();
        log.info("Deleting task id={} for user id={}", id, user.getId());

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (!task.getOwner().getId().equals(user.getId())) {
            log.warn("User id={} tried to delete task id={} owned by user id={}",
                    user.getId(), id, task.getOwner().getId());
            throw new ForbiddenException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
        log.debug("Task id={} deleted by user id={}", id, user.getId());
    }

    private TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .dueDate(task.getDueDate() != null ? task.getDueDate().toString() : null)
                .ownerId(task.getOwner().getId())
                .build();
    }
}
