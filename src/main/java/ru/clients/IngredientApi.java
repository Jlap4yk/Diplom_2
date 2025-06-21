package ru.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import java.util.List;

import static io.restassured.RestAssured.given;

@UtilityClass
public class IngredientApi {
    // Конечная точка API для ингредиентов
    private static final String INGREDIENTS_ENDPOINT = "/api/ingredients";

    // Получение списка ингредиентов
    @Step("Получение списка ингредиентов")
    public static Response getIngredients() {
        return given()
                .when()
                .get(INGREDIENTS_ENDPOINT);
    }

    // Получение идентификаторов ингредиентов
    @Step("Извлечение идентификаторов ингредиентов")
    public static List<String> fetchIngredientIds() {
        return getIngredients()
                .jsonPath()
                .getList("data._id");
    }
}