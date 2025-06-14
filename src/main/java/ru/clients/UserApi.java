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
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String TOKEN_ENDPOINT = "/api/auth/token";

    @Step("Регистрация пользователя")
    public static Response registerUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Регистрация случайного пользователя")
    public static String registerRandomUser() {
        User user = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@yandex.ru")
                .password("StrongPassword123!")
                .name("Test User")
                .build();

        Response response = registerUser(user);
        return response.path("accessToken");
    }

    @Step("Авторизация пользователя")
    public static Response login(Credentials credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    @Step("Выход из системы")
    public static Response logout(TokenRequest tokenRequest) {
        return given()
                .contentType("application/json")
                .body(tokenRequest)
                .when()
                .post(LOGOUT_ENDPOINT);
    }

    @Step("Обновление токена")
    public static Response refreshToken(TokenRequest tokenRequest) {
        return given()
                .contentType("application/json")
                .body(tokenRequest)
                .when()
                .post(TOKEN_ENDPOINT);
    }
}
