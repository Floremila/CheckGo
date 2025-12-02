package se.floremila.checkgo.service;

import se.floremila.checkgo.dto.TaskRequestDTO;
import se.floremila.checkgo.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {

    List<TaskResponseDTO> getMyTasks();

    TaskResponseDTO getMyTaskById(Long id);

    TaskResponseDTO createTask(TaskRequestDTO request);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);

    void deleteTask(Long id);
}

