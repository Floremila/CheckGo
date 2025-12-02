package se.floremila.checkgo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String dueDate;
    private Long ownerId;
}

