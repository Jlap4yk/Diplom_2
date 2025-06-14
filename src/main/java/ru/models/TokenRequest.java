package ru.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// Запрос с токеном
public class TokenRequest {
    private String token;
}
