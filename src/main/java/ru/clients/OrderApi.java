package ru.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import ru.models.OrderRequest;

import java.util.List;

import static io.restassured.RestAssured.given;

@UtilityClass
public class OrderApi {
    // Конечная точка API для заказов
    private static final String ORDERS_ENDPOINT = "/api/orders";

    // Создание заказа с авторизацией
    @Step("Создание заказа с авторизацией")
    public static Response submitOrderWithAuth(String authToken, List<String> ingredients) {
        return given()
                .header("Authorization", authToken)
                .contentType("application/json")
                .body(new OrderRequest(ingredients))
                .when()
                .post(ORDERS_ENDPOINT);
    }

    // Создание заказа без авторизации
    @Step("Создание заказа без авторизации")
    public static Response submitOrderWithoutAuth(List<String> ingredients) {
        return given()
                .contentType("application/json")
                .body(new OrderRequest(ingredients))
                .when()
                .post(ORDERS_ENDPOINT);
    }

    // Получение заказов пользователя
    @Step("Получение заказов пользователя")
    public static Response fetchUserOrders(String authToken) {
        return given()
                .header("Authorization", authToken)
                .when()
                .get(ORDERS_ENDPOINT);
    }
}