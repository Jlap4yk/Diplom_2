import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.clients.UserApi;
import ru.models.User;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

@Epic("Stellar Burgers API")
@Feature("Тестирование регистрации пользователей")
public class UserRegistrationTest extends BaseApiTest {

    private User newUser;
    private String authToken;

    // Инициализация тестового пользователя
    @Before
    public void setupUserData() {
        super.configureRestAssured();
        newUser = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password("SecurePass123!")
                .name("Test User")
                .build();
    }

    // Очистка тестовых данных
    @After
    public void cleanupUserData() {
        if (authToken != null && !authToken.isEmpty()) {
            cleanupUser(authToken);
        }
    }

    @Test
    @DisplayName("Проверка успешной регистрации уникального пользователя")
    public void checkUniqueUserCreation() {
        Response response = UserApi.createUserAccount(newUser);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        authToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Проверка ошибки при регистрации дублирующего пользователя")
    public void checkDuplicateUserCreationFailure() {
        Response firstResponse = UserApi.createUserAccount(newUser);
        authToken = firstResponse.path("accessToken");

        UserApi.createUserAccount(newUser)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверка ошибки при регистрации без пароля")
    public void checkUserCreationFailureWithoutPassword() {
        User invalidUser = createInvalidUser("password");
        validateInvalidUserCreation(invalidUser);
    }

    @Test
    @DisplayName("Проверка ошибки при регистрации без email")
    public void checkUserCreationFailureWithoutEmail() {
        User invalidUser = createInvalidUser("email");
        validateInvalidUserCreation(invalidUser);
    }

    @Test
    @DisplayName("Проверка ошибки при регистрации без имени")
    public void checkUserCreationFailureWithoutName() {
        User invalidUser = createInvalidUser("name");
        validateInvalidUserCreation(invalidUser);
    }

    // Вспомогательный метод для создания пользователя с отсутствующим полем
    private User createInvalidUser(String missingField) {
        User.UserBuilder builder = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password("SecurePass123!")
                .name("Test User");

        switch (missingField) {
            case "email":
                return builder.email(null).build();
            case "password":
                return builder.password(null).build();
            case "name":
                return builder.name(null).build();
            default:
                return builder.build();
        }
    }

    // Вспомогательный метод для проверки ошибки регистрации
    private void validateInvalidUserCreation(User user) {
        UserApi.createUserAccount(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}