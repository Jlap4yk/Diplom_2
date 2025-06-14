package ru.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import java.util.List;

import static io.restassured.RestAssured.given;

@UtilityClass
public class IngredientApi {
    private static final String INGREDIENTS_ENDPOINT = "/api/ingredients";

    @Step("Получение списка ингредиентов")
    public static Response getIngredients() {
        return given()
                .when()
                .get(INGREDIENTS_ENDPOINT);
    }

    @Step("Получение валидных ID ингредиентов")
    public static List<String> getValidIngredientIds() {
        return getIngredients()
                .jsonPath()
                .getList("data._id");
    }
}
