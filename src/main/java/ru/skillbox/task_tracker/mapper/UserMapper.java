package ru.skillbox.task_tracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.web.model.UserRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", expression = "java(generateId())")
    User toEntity(UserRequest user);

    UserResponse toDto(User user);

    User toUser(UserResponse userResponse);

    default String generateId() {
        return java.util.UUID.randomUUID().toString();
    }
}
