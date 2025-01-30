package ru.skillbox.task_tracker.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.task_tracker.entity.TaskStatus;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Название задачи ('name') должно быть заполнено!")
    @Size(min = 3, max = 30, message = "Имя не может быть меньше {min} и больше {max}!")
    private String name;

    private String description;

    @NotBlank(message = "Исполнитель ('assigneeId') должен быть заполнен!")
    private String assigneeId;

    private Set<String> observerIds;
    
    private TaskStatus status;
}
