package ru.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
// Запрос на создание заказа
public class OrderRequest {
    private List<String> ingredients;
}
