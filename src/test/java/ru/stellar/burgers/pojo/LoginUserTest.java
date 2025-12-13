package ru.stellar.burgers.pojo;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.stellar.burgers.pojo.User;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Тестовый класс для проверки аутентификации пользователей через API Stellar Burgers.
 *
 * @author Irina_Zakirova
 */
public class LoginUserTest {
    /**
     * Базовый URL API Stellar Burgers.
     */
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";
    /**
     * Тестовый пользователь, создаваемый перед каждым тестом.
     */
    private User testUser;
    /**
     * Access token пользователя, полученный при успешной регистрации.
     */
    private String accessToken;

    @Step("Создание пользователя")
    private Response createUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Логин пользователя")
    private Response loginUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/login");
    }

    @Step("Удаление пользователя")
    private void deleteUser(String token) {
        if (token != null) {
            given()
                    .header("Authorization", token)
                    .baseUri(BASE_URL)
                    .when()
                    .delete("/auth/user");
        }
    }

    @BeforeEach
    void setUp() {
        testUser = new User(
                "test" + System.currentTimeMillis() + "@mail.com",
                "password123",
                "TestUser"
        );

        Response response = createUser(testUser);
        accessToken = response.path("accessToken");
    }

    @AfterEach
    void tearDown() {
        deleteUser(accessToken);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    void loginWithValidCredentials() {
        Response response = loginUser(testUser);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testUser.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    void loginWithInvalidCredentials() {
        User wrongUser = new User("wrong@mail.com", "wrongpassword", "WrongUser");
        Response response = loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с правильным email и неверным паролем")
    void loginWithValidEmailAndInvalidPassword() {
        User wrongPasswordUser = new User(testUser.getEmail(), "wrongpassword", "TestUser");
        Response response = loginUser(wrongPasswordUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}