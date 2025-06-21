import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;

import static io.restassured.RestAssured.given;

public abstract class BaseApiTest {
    // Базовый URL сервиса Stellar Burgers
    protected static final String API_BASE_URL = "https://stellarburgers.nomoreparties.site";
    // Конечная точка для операций с пользователем
    protected static final String USER_API_ENDPOINT = "/api/auth/user";

    // Настройка RestAssured перед запуском тестов
    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = API_BASE_URL;
        // Включение фильтра Allure для отчетов
        RestAssured.filters(new AllureRestAssured());
        // Логирование запросов и ответов при ошибках
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // Удаление тестового пользователя
    @Step("Удаление тестового пользователя из системы")
    protected void cleanupUser(String authToken) {
        if (authToken != null && !authToken.isEmpty()) {
            given()
                    .header("Authorization", authToken)
                    .when()
                    .delete(USER_API_ENDPOINT)
                    .then()
                    .statusCode(202);
        }
    }
}