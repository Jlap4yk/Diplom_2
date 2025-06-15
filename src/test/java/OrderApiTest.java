import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.clients.IngredientApi;
import ru.clients.OrderApi;
import ru.clients.UserApi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;

@Epic("Stellar Burgers API")
@Feature("Тестирование заказов и ингредиентов")
public class OrderApiTest extends BaseApiTest {

    private String authToken;
    private List<String> ingredientIds;

    // Подготовка данных для тестов
    @Before
    public void setupTestEnvironment() {
        super.configureRestAssured();
        ingredientIds = IngredientApi.fetchIngredientIds();
        authToken = UserApi.createRandomUser();
    }

    // Очистка данных после тестов
    @After
    public void cleanupEnvironment() {
        if (authToken != null) {
            cleanupUser(authToken);
        }
    }

    @Test
    @DisplayName("Проверка получения списка ингредиентов")
    public void checkIngredientsListRetrieval() {
        IngredientApi.getIngredients()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .body("data[0]._id", not(emptyString()))
                .body("data[0].name", not(emptyString()))
                .body("data[0].type", not(emptyString()));
    }

    @Test
    @DisplayName("Проверка создания заказа с авторизацией")
    public void checkOrderCreationWithAuth() {
        submitOrder(authToken, ingredientIds.subList(0, 2))
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", not(emptyString()))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Проверка создания заказа без авторизации")
    public void checkOrderCreationWithoutAuth() {
        OrderApi.submitOrderWithoutAuth(ingredientIds.subList(0, 2))
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверка ошибки при создании заказа без ингредиентов")
    public void checkOrderCreationFailureWithoutIngredients() {
        OrderApi.submitOrderWithoutAuth(Collections.emptyList())
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверка ошибки при создании заказа с невалидными ингредиентами")
    public void checkOrderCreationFailureWithInvalidIngredients() {
        OrderApi.submitOrderWithoutAuth(Arrays.asList("invalid_id_1", "invalid_id_2"))
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("One or more ids provided are incorrect"));
    }

    @Test
    @DisplayName("Проверка получения заказов пользователя")
    public void checkUserOrdersRetrieval() {
        submitOrder(authToken, ingredientIds.subList(0, 2))
                .then()
                .statusCode(SC_OK);

        OrderApi.fetchUserOrders(authToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("orders[0].number", notNullValue())
                .body("orders[0].ingredients", not(empty()));
    }

    // Вспомогательный метод для создания заказа
    private Response submitOrder(String token, List<String> ingredients) {
        return OrderApi.submitOrderWithAuth(token, ingredients);
    }
}