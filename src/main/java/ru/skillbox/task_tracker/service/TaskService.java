package ru.skillbox.task_tracker.service;

import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.web.model.TaskRequest;
import ru.skillbox.task_tracker.web.model.TaskResponse;
import ru.skillbox.task_tracker.web.model.TaskUpdateRequest;

public interface TaskService {

    Flux<TaskResponse> findAll();

    Mono<TaskResponse> findById(String id);

    Mono<TaskResponse> create(TaskRequest task, UserDetails userdetails);

    Mono<TaskResponse> update(String id, TaskUpdateRequest updatedTask);

    Mono<Void> deleteById(String id);

    Mono<TaskResponse> addObserver(String id, String observerId);
}
