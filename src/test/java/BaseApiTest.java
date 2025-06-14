import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;

import static io.restassured.RestAssured.given;

public abstract class BaseApiTest {
    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    protected static final String USER_ENDPOINT = "/api/auth/user";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Step("Удаление тестового пользователя")
    protected void deleteUser(String accessToken) {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .when()
                    .delete(USER_ENDPOINT)
                    .then()
                    .statusCode(202);
        }
    }
}