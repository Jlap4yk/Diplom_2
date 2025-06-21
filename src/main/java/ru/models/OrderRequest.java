package ru.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// Модель запроса для создания заказа
@Data
@AllArgsConstructor
public class OrderRequest {
    private List<String> ingredients; // Список идентификаторов ингредиентов
}