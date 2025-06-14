package ru.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import ru.models.OrderRequest;

import java.util.List;

import static io.restassured.RestAssured.given;

@UtilityClass
public class OrderApi {
    private static final String ORDERS_ENDPOINT = "/api/orders";

    @Step("Создание заказа с авторизацией")
    public static Response createOrder(String accessToken, List<String> ingredients) {
        return given()
                .header("Authorization", accessToken)
                .contentType("application/json")
                .body(new OrderRequest(ingredients))
                .when()
                .post(ORDERS_ENDPOINT);
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutAuth(List<String> ingredients) {
        return given()
                .contentType("application/json")
                .body(new OrderRequest(ingredients))
                .when()
                .post(ORDERS_ENDPOINT);
    }

    @Step("Получение заказов пользователя")
    public static Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_ENDPOINT);
    }
}
