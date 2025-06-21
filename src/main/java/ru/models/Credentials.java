package ru.models;

import lombok.AllArgsConstructor;
import lombok.Data;

// Модель учетных данных для авторизации
@Data
@AllArgsConstructor
public class Credentials {
    private String email;    // Электронная почта для входа
    private String password; // Пароль для входа
}