package ru.models;

import lombok.AllArgsConstructor;
import lombok.Data;

// Модель запроса для операций с токеном
@Data
@AllArgsConstructor
public class TokenRequest {
    private String token; // Токен для выхода или обновления
}