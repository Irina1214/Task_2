package ru.stellar.burgers.pojo;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Тестовый класс для проверки создания пользователя через API Stellar Burgers.
 *
 * @author Irina_Zakirova
 */
public class CreateUserTest {
    /**
     * Базовый URL API Stellar Burgers.
     */
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";
    /**
     * Access token пользователя, полученный при успешной регистрации.
     */
    private String accessToken;
    /**
     * Тестовый пользователь, создаваемый перед каждым тестом.
     */
    private User testUser;

    /**
     * Генерирует уникальный email для тестовых пользователей.
     * @return уникальный email в формате "test{timestamp}@mail.com"
     */
    private String generateUniqueEmail() {
        return "test" + System.currentTimeMillis() + "@mail.com";
    }

    @Step("Создание пользователя")
    private Response createUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/register");
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
                generateUniqueEmail(),
                "password123",
                "TestUser"
        );
    }

    @AfterEach
    void tearDown() {
        deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    void createUniqueUser() {
        Response response = createUser(testUser);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(testUser.getName()));
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    void createDuplicateUser() {
        Response firstResponse = createUser(testUser);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = createUser(testUser);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    void createUserWithoutEmail() {
        User user = new User(null, "password123", "TestUser");

        Response response = createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    void createUserWithoutPassword() {
        User user = new User(generateUniqueEmail(), null, "TestUser");

        Response response = createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    void createUserWithoutName() {
        User user = new User(generateUniqueEmail(), "password123", null);

        Response response = createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}