package ru.skillbox.task_tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;

    private TaskStatus status;

    @ReadOnlyProperty
    private User author;

    @ReadOnlyProperty
    private User assignee;

    @ReadOnlyProperty
    private Set<User> observers = new HashSet<>();
}
