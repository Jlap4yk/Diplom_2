import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
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
@Feature("Работа с заказами и ингредиентами")
public class OrderApiTest extends BaseApiTest {

    private String accessToken;
    private List<String> validIngredientIds;

    @Before
    public void setUp() {
        super.setUp();
        validIngredientIds = IngredientApi.getValidIngredientIds();
        accessToken = UserApi.registerRandomUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение списка ингредиентов")
    public void testGetIngredientsList() {
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
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    public void testCreateOrderWithAuthAndValidIngredients() {
        OrderApi.createOrder(accessToken, validIngredientIds.subList(0, 2))
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", not(emptyString()))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        OrderApi.createOrderWithoutAuth(validIngredientIds.subList(0, 2))
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        OrderApi.createOrderWithoutAuth(Collections.emptyList())
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидными ингредиентами")
    public void testCreateOrderWithInvalidIngredients() {
        OrderApi.createOrderWithoutAuth(Arrays.asList("invalid_hash_1", "invalid_hash_2"))
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Получение заказов пользователя")
    public void testGetUserOrders() {
        // Создаем заказ для пользователя
        OrderApi.createOrder(accessToken, validIngredientIds.subList(0, 2))
                .then()
                .statusCode(SC_OK);

        OrderApi.getUserOrders(accessToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("orders[0].number", notNullValue())
                .body("orders[0].ingredients", not(empty()));
    }
}

