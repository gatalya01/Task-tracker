package ru.skillbox.task_tracker.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.Task;
import ru.skillbox.task_tracker.entity.TaskStatus;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.exception.EntityNotFoundException;
import ru.skillbox.task_tracker.mapper.TaskMapper;
import ru.skillbox.task_tracker.mapper.UserMapper;
import ru.skillbox.task_tracker.repository.TaskRepository;
import ru.skillbox.task_tracker.repository.UserRepository;
import ru.skillbox.task_tracker.service.TaskService;
import ru.skillbox.task_tracker.service.UserService;
import ru.skillbox.task_tracker.web.model.TaskRequest;
import ru.skillbox.task_tracker.web.model.TaskResponse;
import ru.skillbox.task_tracker.web.model.TaskUpdateRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final UserService userService;


    @Override
    public Flux<TaskResponse> findAll() {
        Flux<Task> all = taskRepository.findAll();
        return all
                .flatMap(task -> {
                    validateTask(task);
                    Mono<User> authorMono = userService.findById(task.getAuthorId())
                            .map(userMapper::toUser);
                    Mono<User> assigneeMono = userService.findById(task.getAssigneeId())
                            .map(userMapper::toUser);
                    Flux<User> observersFlux = userService.findAllById(task.getObserverIds());

                    return taskMapper.toDto(task, authorMono, assigneeMono, observersFlux, userMapper);
                });
    }

    @Override
    public Mono<TaskResponse> findById(String id) {
        Mono<Task> taskResponseMono = taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Task not found with id: " + id)));
        return taskResponseMono
                .flatMap(task -> {
                    validateTask(task);
                    Mono<User> authorMono = userService.findById(task.getAuthorId())
                            .map(userMapper::toUser);

                    Mono<User> assigneeMono = userService.findById(task.getAssigneeId())
                            .map(userMapper::toUser);
                    Flux<User> observersFlux = userService.findAllById(task.getObserverIds());

                    return taskMapper.toDto(task, authorMono, assigneeMono, observersFlux, userMapper);
                });
    }

    @Override
    public Mono<TaskResponse> create(TaskRequest taskRequest, UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found with username: " + userDetails.getUsername())))
                .flatMap(author ->
                        userRepository.findById(taskRequest.getAssigneeId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Assignee not found with id: " + taskRequest.getAssigneeId())))
                                .flatMap(assignee -> {
                                    Task task = taskMapper.toEntity(taskRequest);
                                    task.setCreatedAt(Instant.now());
                                    task.setUpdatedAt(Instant.now());
                                    task.setAuthorId(author.getId());
                                    task.setAssigneeId(taskRequest.getAssigneeId());
                                    if (taskRequest.getObserverIds() != null) {
                                        task.setObserverIds(taskRequest.getObserverIds());
                                    } else {
                                        task.setObserverIds(new HashSet<>());
                                    }
                                    if (task.getStatus() == null) {
                                        task.setStatus(TaskStatus.TODO);
                                    }

                                    return taskRepository.save(task)
                                            .flatMap(savedTask -> findById(savedTask.getId()));
                                })
                );
    }


    @Override
    public Mono<TaskResponse> update(String id, TaskUpdateRequest taskRequest) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Task not found with id: " + id)))
                .flatMap(existingTask -> {
                    Task updatedTask = updateTaskFields(existingTask, taskRequest);
                    updatedTask.setUpdatedAt(Instant.now());
                    return taskRepository.save(updatedTask);
                })
                .flatMap(savedTask -> findById(savedTask.getId()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Task not found with id: " + id)))
                .flatMap(existingTask -> taskRepository.deleteById(id));
    }


    @Override
    public Mono<TaskResponse> addObserver(String taskId, String observerId) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Task not found with id: " + taskId)))
                .flatMap(task ->
                        userRepository.findById(observerId)
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Observer not found with id: " + observerId)))
                                .flatMap(observer -> {
                                    task.getObserverIds().add(observerId);
                                    task.getObservers().add(observer);
                                    return taskRepository.save(task);
                                })
                )
                .flatMap(savedTask -> findById(savedTask.getId()));
    }


    public void validateTask(Task task) {
        if (task.getAuthorId() == null) {
            throw new EntityNotFoundException("Author ID is missing");
        }
        if (task.getAssigneeId() == null) {
            throw new EntityNotFoundException("Assignee ID is missing");
        }
        if (task.getObserverIds() == null) {
            throw new EntityNotFoundException("Observer IDs are missing");
        }
    }

    private Task updateTaskFields(Task existingTask, TaskUpdateRequest taskRequest) {
        Task updatedTask = new Task();
        updatedTask.setId(existingTask.getId());
        updatedTask.setAuthorId(existingTask.getAuthorId());
        updatedTask.setAssigneeId(taskRequest.getAssigneeId() != null ? taskRequest.getAssigneeId() : existingTask.getAssigneeId());
        updatedTask.setObserverIds(taskRequest.getObserverIds() != null ? taskRequest.getObserverIds() : existingTask.getObserverIds());
        updatedTask.setName(taskRequest.getName() != null ? taskRequest.getName() : existingTask.getName());
        updatedTask.setDescription(taskRequest.getDescription() != null ? taskRequest.getDescription() : existingTask.getDescription());
        updatedTask.setCreatedAt(existingTask.getCreatedAt());
        updatedTask.setStatus(taskRequest.getStatus() != null ? taskRequest.getStatus() : existingTask.getStatus());
        return updatedTask;
    }
}
