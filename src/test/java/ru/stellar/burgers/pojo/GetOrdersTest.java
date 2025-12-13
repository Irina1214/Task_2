package ru.stellar.burgers.pojo;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Тестовый класс для получения заказов пользователя в API Stellar Burgers.
 */
public class GetOrdersTest {
    /**
     * Базовый URL API Stellar Burgers.
     */
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";
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

    @Step("Создание заказа")
    private Response createOrder(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/orders");
    }

    @Step("Получение заказов пользователя")
    private Response getUserOrders(String token) {
        if (token != null) {
            return given()
                    .header("Authorization", token)
                    .baseUri(BASE_URL)
                    .when()
                    .get("/orders");
        } else {
            return given()
                    .baseUri(BASE_URL)
                    .when()
                    .get("/orders");
        }
    }

    @Step("Получение ингредиентов")
    private List<String> getIngredients() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .get("/ingredients");

        return response.path("data._id");
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
        User testUser = new User(
                "test" + System.currentTimeMillis() + "@mail.com",
                "password123",
                "TestUser"
        );

        Response response = createUser(testUser);
        accessToken = response.path("accessToken");

        List<String> ingredients = getIngredients();
        Order order = new Order(ingredients.subList(0, Math.min(3, ingredients.size())));

        Response orderResponse = createOrder(order);
        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @AfterEach
    void tearDown() {
        deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    void getUserOrdersWithAuth() {
        Response response = getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    void getUserOrdersWithoutAuth() {
        Response response = getUserOrders(null);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}