package ru.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import ru.models.Credentials;
import ru.models.TokenRequest;
import ru.models.User;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@UtilityClass
public class UserApi {
    // Конечные точки API для пользователей
    private static final String REGISTRATION_ENDPOINT = "/api/auth/register";
    private static final String AUTH_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String TOKEN_ENDPOINT = "/api/auth/token";

    // Создание новой учетной записи
    @Step("Создание нового пользователя")
    public static Response createUserAccount(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(REGISTRATION_ENDPOINT);
    }

    // Создание случайного пользователя
    @Step("Создание случайного пользователя")
    public static String createRandomUser() {
        User randomUser = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password("SecurePass123!")
                .name("Test User")
                .build();

        Response response = createUserAccount(randomUser);
        return response.path("accessToken");
    }

    // Авторизация пользователя
    @Step("Авторизация пользователя")
    public static Response authenticateUser(Credentials credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post(AUTH_ENDPOINT);
    }

    // Выход из системы
    @Step("Выход пользователя из системы")
    public static Response signOut(TokenRequest tokenRequest) {
        return given()
                .contentType("application/json")
                .body(tokenRequest)
                .when()
                .post(LOGOUT_ENDPOINT);
    }

    // Обновление токена
    @Step("Обновление токена авторизации")
    public static Response renewAuthToken(TokenRequest tokenRequest) {
        return given()
                .contentType("application/json")
                .body(tokenRequest)
                .when()
                .post(TOKEN_ENDPOINT);
    }
}