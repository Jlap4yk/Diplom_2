package ru.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// Учетные данные
public class Credentials {
    private String email;
    private String password;
}
