package ru.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Модель данных пользователя для API
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String email;    // Электронная почта пользователя
    private String password; // Пароль пользователя
    private String name;     // Имя пользователя
}