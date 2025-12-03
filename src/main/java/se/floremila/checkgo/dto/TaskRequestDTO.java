package se.floremila.checkgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequestDTO {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    private String status;

    private String dueDate;
}
