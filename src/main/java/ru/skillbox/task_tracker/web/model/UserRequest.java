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
public class UserRequest {

    @NotBlank(message = "Имя ('username') должно быть заполнено!")
    @Size(min = 3, max = 30, message = "Имя не может быть меньше {min} и больше {max}!")
    private String username;


    @NotBlank(message = "Пароль ('password') должен быть заполнен!")
    @Size(min = 8, max = 30, message = "Пароль не может быть меньше {min} и больше {max}!")
    private String password;

    @Email
    private String email;
}
