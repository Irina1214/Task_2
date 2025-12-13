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
 * Тестовый класс для проверки создания заказов в API Stellar Burgers.
 */
public class CreateOrderTest {
    /**
     * Базовый URL API Stellar Burgers.
     */
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";
    /**
     * Access token пользователя, полученный при успешной регистрации.
     */
    private String accessToken;
    /**
     * Список доступных ингредиентов, полученный из API.
     */
    private List<String> ingredients;

    @Step("Создание пользователя")
    private Response createUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Создание заказа с авторизацией")
    private Response createOrderWithAuth(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/orders");
    }

    @Step("Создание заказа без авторизации")
    private Response createOrderWithoutAuth(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/orders");
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
        ingredients = getIngredients();
    }

    @AfterEach
    void tearDown() {
        deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    void createOrderWithAuthAndIngredients() {
        Order order = new Order(ingredients.subList(0, Math.min(3, ingredients.size())));

        Response response = createOrderWithAuth(order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    void createOrderWithoutAuthWithIngredients() {
        Order order = new Order(ingredients.subList(0, Math.min(3, ingredients.size())));

        Response response = createOrderWithoutAuth(order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    void createOrderWithoutIngredients() {
        Order order = new Order(null);

        Response response = createOrderWithAuth(order);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    void createOrderWithInvalidIngredientHash() {
        Order order = new Order(List.of("invalidhash12345"));

        Response response = createOrderWithAuth(order);

        response.then()
                .statusCode(500);
    }
}