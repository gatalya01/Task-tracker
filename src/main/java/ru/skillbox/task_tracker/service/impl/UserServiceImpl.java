package ru.skillbox.task_tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.RoleType;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.exception.EntityNotFoundException;
import ru.skillbox.task_tracker.mapper.UserMapper;
import ru.skillbox.task_tracker.repository.TaskRepository;
import ru.skillbox.task_tracker.repository.UserRepository;
import ru.skillbox.task_tracker.service.UserService;
import ru.skillbox.task_tracker.web.model.UserRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;
import ru.skillbox.task_tracker.web.model.UserUpdateRequest;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Flux<UserResponse> findAll() {

        return userRepository.findAll().map(userMapper::toDto);
    }

    @Override
    public Mono<UserResponse> findById(String id) {

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + id)))
                .map(userMapper::toDto);

    }

    @Override
    public Mono<UserResponse> create(UserRequest userRequest, RoleType roleType) {
        User user = userMapper.toEntity(userRequest);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.getRoles().add(roleType);

        return userRepository.save(user)
                .map(userMapper::toDto);
    }

    @Override
    public Mono<UserResponse> update(String id, UserUpdateRequest userDto) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + id)))
                .flatMap(user -> {
                    if (userDto.getUsername() != null) {
                        user.setUsername(userDto.getUsername());
                    }

                    if (userDto.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    }

                    if (userDto.getEmail() != null) {
                        user.setEmail(userDto.getEmail());
                    }

                    return userRepository.save(user);
                })
                .map(userMapper::toDto);
    }


    @Override
    public Mono<Void> deleteById(String id) {
        return userRepository.findById(id)
                .flatMap(user -> {

                    return taskRepository.findAllByAuthorId(id)
                            .concatWith(taskRepository.findAllByAssigneeId(id))
                            .concatWith(taskRepository.findAllByObserverIdsContaining(id))
                            .flatMap(task -> taskRepository.deleteById(task.getId()))
                            .then(userRepository.deleteById(id));
                })
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found")));
    }


    @Override
    public Flux<User> findAllById(Set<String> observerIds) {
        return userRepository.findAllById(observerIds)
                .collectList()
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        log.warn("No users found with ids: {}", observerIds);
                        return Flux.empty();
                    } else {
                        return Flux.fromIterable(users);
                    }
                });
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
