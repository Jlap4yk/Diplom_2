package ru.models;

import lombok.Data;

// Модель ответа API с ошибкой
@Data
public class ErrorResponse {
    private boolean success; // Статус выполнения запроса
    private String message;  // Сообщение об ошибке
}