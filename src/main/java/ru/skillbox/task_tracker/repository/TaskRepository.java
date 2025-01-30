package ru.skillbox.task_tracker.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.Task;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    Flux<Task> findByAssigneeId(String assigneeId);

    Flux<Task> findAllByAuthorId(String authorId);

    Flux<Task> findAllByAssigneeId(String assigneeId);

    Flux<Task> findAllByObserverIdsContaining(String observerId);
}
