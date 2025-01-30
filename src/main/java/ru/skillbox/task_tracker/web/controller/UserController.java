package ru.skillbox.task_tracker.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.RoleType;
import ru.skillbox.task_tracker.service.UserService;
import ru.skillbox.task_tracker.web.model.UserRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;
import ru.skillbox.task_tracker.web.model.UserUpdateRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return userService.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(ResponseEntity::ok);
    }


    @PostMapping("/create")
    public Mono<ResponseEntity<UserResponse>> createUser(@Valid @RequestBody UserRequest user, @RequestParam RoleType roleType) {
        return userService.create(user, roleType)
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(userResponse))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateRequest user) {
        return userService.update(id, user)
                .map(ResponseEntity::ok);
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return userService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}