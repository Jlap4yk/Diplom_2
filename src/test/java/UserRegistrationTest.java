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

@Epic("API Stellar Burgers")
@Feature("Регистрация пользователя")
public class UserRegistrationTest extends BaseApiTest {

    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        super.setUp();
        testUser = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password("StrongPassword123!")
                .name("Test User")
                .build();
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешная регистрация уникального пользователя")
    public void testCreateUniqueUserSuccessfully() {
        Response response = UserApi.registerUser(testUser);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Регистрация существующего пользователя")
    public void testCreateDuplicateUserFails() {
        // Первая регистрация - должна быть успешной
        Response firstRegistration = UserApi.registerUser(testUser);
        accessToken = firstRegistration.path("accessToken");

        // Попытка повторной регистрации
        UserApi.registerUser(testUser)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void testCreateUserWithoutPasswordFails() {
        User userWithoutPassword = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password(null)
                .name("Test User")
                .build();

        UserApi.registerUser(userWithoutPassword)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmailFails() {
        User userWithoutEmail = User.builder()
                .email(null)
                .password("ValidPassword123!")
                .name("Test User")
                .build();

        UserApi.registerUser(userWithoutEmail)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void testCreateUserWithoutNameFails() {
        User userWithoutName = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password("ValidPassword123!")
                .name(null)
                .build();

        UserApi.registerUser(userWithoutName)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

