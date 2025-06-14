package ru.models;

import lombok.Data;

@Data
// Ошибка API
public class ErrorResponse {
    private boolean success;
    private String message;
}
