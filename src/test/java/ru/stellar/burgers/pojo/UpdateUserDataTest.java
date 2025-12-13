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
 * Тестовый класс для проверки обновления данных пользователя через API Stellar Burgers.
 *
 * @author Irina_Zakirova
 */
public class UpdateUserDataTest {
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
    @DisplayName("Изменение email с авторизацией")
    void updateEmailWithAuth() {
        User updatedUser = new User();
        updatedUser.setEmail("updated" + System.currentTimeMillis() + "@mail.com");

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(updatedUser)
                .when()
                .patch("/auth/user");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение имени с авторизацией")
    void updateNameWithAuth() {
        User updatedUser = new User();
        updatedUser.setName("UpdatedName" + System.currentTimeMillis());

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(updatedUser)
                .when()
                .patch("/auth/user");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    void updatePasswordWithAuth() {
        String newPassword = "newpassword" + System.currentTimeMillis();
        User updatedUser = new User();
        updatedUser.setPassword(newPassword);

        Response updateResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(updatedUser)
                .when()
                .patch("/auth/user");

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        Response loginResponse = loginUser(new User(testUser.getEmail(), newPassword, testUser.getName()));

        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    void updateUserWithoutAuth() {
        User updatedUser = new User("test@mail.com", "password", "name");

        Response response = given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(updatedUser)
                .when()
                .patch("/auth/user");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}