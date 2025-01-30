package ru.skillbox.task_tracker.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.task_tracker.entity.TaskStatus;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;

    private TaskStatus status;

    private UserResponse author;
    private UserResponse assignee;
    private Set<UserResponse> observers;
}
