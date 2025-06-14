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
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyString;

@Epic("Stellar Burgers API")
@Feature("Авторизация и регистрация пользователей")
public class AuthApiTest extends BaseApiTest {

    private User testUser;
    private String accessToken;
    private String refreshToken;
    private Faker faker;
    private String correctPassword;

    @Before
    public void setUp() {
        super.setUp();
        faker = new Faker();

        correctPassword = faker.internet().password(8, 16, true, true, true);
        testUser = User.builder()
                .email(faker.internet().emailAddress())
                .password(correctPassword)
                .name(faker.name().fullName())
                .build();

        // Регистрируем пользователя перед тестами
        Response registerResponse = UserApi.registerUser(testUser);
        registerResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        accessToken = registerResponse.path("accessToken");
        refreshToken = registerResponse.path("refreshToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void testSuccessfulUserRegistration() {
        User newUser = User.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(8, 16, true, true, true))
                .name(faker.name().fullName())
                .build();

        Response response = UserApi.registerUser(newUser);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", not(emptyString()))
                .body("refreshToken", not(emptyString()))
                .body("user.email", equalTo(newUser.getEmail()))
                .body("user.name", equalTo(newUser.getName()));

        // Удаляем созданного пользователя после теста
        String newUserToken = response.path("accessToken");
        deleteUser(newUserToken);
    }

    @Test
    @DisplayName("Успешный вход существующего пользователя")
    public void testSuccessfulLogin() {
        UserApi.login(new Credentials(testUser.getEmail(), testUser.getPassword()))
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", not(emptyString()))
                .body("refreshToken", not(emptyString()))
                .body("user.email", equalTo(testUser.getEmail()))
                .body("user.name", equalTo(testUser.getName()));
    }

    @Test
    @DisplayName("Неудачный вход: правильный email, но неверный пароль")
    public void testFailedLoginWithCorrectEmailButWrongPassword() {
        String wrongPassword = correctPassword + "!"; // Делаем заведомо неверный пароль

        UserApi.login(new Credentials(testUser.getEmail(), wrongPassword))
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Неудачный вход: неверный email, но правильный пароль")
    public void testFailedLoginWithWrongEmailButCorrectPassword() {
        String wrongEmail = "wrong_" + testUser.getEmail();

        UserApi.login(new Credentials(wrongEmail, correctPassword))
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}


