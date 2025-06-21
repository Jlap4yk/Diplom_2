import com.github.javafaker.Faker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.clients.UserApi;
import ru.models.Credentials;
import ru.models.User;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

@Epic("Stellar Burgers API")
@Feature("Тестирование авторизации и регистрации")
public class AuthApiTest extends BaseApiTest {

    private User testUser;
    private String authToken;
    private String refreshToken;
    private Faker faker;
    private String validPassword;

    // Инициализация тестового окружения
    @Before
    public void initializeTestData() {
        super.configureRestAssured();
        faker = new Faker();
        validPassword = faker.internet().password(8, 16, true, true, true);

        // Создание тестового пользователя
        testUser = User.builder()
                .email(faker.internet().emailAddress())
                .password(validPassword)
                .name(faker.name().fullName())
                .build();

        // Регистрация пользователя
        Response registrationResponse = UserApi.createUserAccount(testUser);
        registrationResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        authToken = registrationResponse.path("accessToken");
        refreshToken = registrationResponse.path("refreshToken");
    }

    // Очистка тестовых данных
    @After
    public void cleanupTestData() {
        if (authToken != null) {
            cleanupUser(authToken);
        }
    }

    @Test
    @DisplayName("Проверка успешной регистрации нового пользователя")
    public void checkSuccessfulRegistration() {
        User newUser = createRandomUser();
        Response response = UserApi.createUserAccount(newUser);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", not(emptyString()))
                .body("refreshToken", not(emptyString()))
                .body("user.email", equalTo(newUser.getEmail()))
                .body("user.name", equalTo(newUser.getName()));

        // Удаление созданного пользователя
        String newUserToken = response.path("accessToken");
        cleanupUser(newUserToken);
    }

    @Test
    @DisplayName("Проверка успешной авторизации пользователя")
    public void checkSuccessfulLogin() {
        UserApi.authenticateUser(new Credentials(testUser.getEmail(), testUser.getPassword()))
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", not(emptyString()))
                .body("refreshToken", not(emptyString()))
                .body("user.email", equalTo(testUser.getEmail()))
                .body("user.name", equalTo(testUser.getName()));
    }

    @Test
    @DisplayName("Проверка ошибки авторизации с неверным паролем")
    public void checkLoginFailureWithWrongPassword() {
        String invalidPassword = validPassword + "!";
        UserApi.authenticateUser(new Credentials(testUser.getEmail(), invalidPassword))
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка ошибки авторизации с неверным email")
    public void checkLoginFailureWithWrongEmail() {
        String invalidEmail = "wrong_" + testUser.getEmail();
        UserApi.authenticateUser(new Credentials(invalidEmail, validPassword))
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    // Вспомогательный метод для создания случайного пользователя
    private User createRandomUser() {
        return User.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(8, 16, true, true, true))
                .name(faker.name().fullName())
                .build();
    }
}