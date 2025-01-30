package ru.skillbox.task_tracker.web.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 3, max = 30, message = "Имя не может быть меньше {min} и больше {max}!")
    private String username;

    private String password;

    @Email
    private String email;
}
