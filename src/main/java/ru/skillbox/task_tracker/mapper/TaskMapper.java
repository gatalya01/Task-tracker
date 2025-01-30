package ru.skillbox.task_tracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.Task;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.web.model.TaskRequest;
import ru.skillbox.task_tracker.web.model.TaskResponse;
import ru.skillbox.task_tracker.web.model.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface TaskMapper {

    TaskResponse toDto(Task task);

    @Mapping(target = "id", expression = "java(generateId())")
    Task toEntity(TaskRequest task);

    default Mono<TaskResponse> toDto(Task task, Mono<User> authorMono, Mono<User> assigneeMono, Flux<User> observersFlux, UserMapper userMapper) {
        return Mono.zip(authorMono, assigneeMono, observersFlux.collectList())
                .map(tuple -> {
                    User author = tuple.getT1();
                    User assignee = tuple.getT2();
                    List<User> observers = tuple.getT3();

                    TaskResponse taskResponse = toDto(task);
                    taskResponse.setAuthor(userMapper.toDto(author));
                    taskResponse.setAssignee(userMapper.toDto(assignee));
                    taskResponse.setObservers(observers.stream()
                            .map(userMapper::toDto)
                            .collect(Collectors.toSet()));
                    return taskResponse;
                });
    }

    default String generateId() {
        return java.util.UUID.randomUUID().toString();
    }
}
