package ru.skillbox.task_tracker.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;;
import ru.skillbox.task_tracker.service.TaskService;
import ru.skillbox.task_tracker.web.model.TaskRequest;
import ru.skillbox.task_tracker.web.model.TaskResponse;
import ru.skillbox.task_tracker.web.model.TaskUpdateRequest;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    public Flux<TaskResponse> getAllTasks() {
        return taskService.findAll();
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@PathVariable String id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> createTask(@RequestBody @Valid TaskRequest task,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        return taskService.create(task, userDetails)
                .map(ResponseEntity::ok);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTask(@PathVariable String id, @RequestBody @Valid TaskUpdateRequest task) {
        return taskService.update(id, task)
                .map(ResponseEntity::ok);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @PatchMapping("/observers/{id}")
    public Mono<ResponseEntity<TaskResponse>> addObserver(@PathVariable String id, @RequestParam String observerId) {
        return taskService.addObserver(id, observerId)
                .map(ResponseEntity::ok);
    }


    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return taskService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}