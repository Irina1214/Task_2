package ru.stellar.burgers.pojo;

import java.util.List;

/**
 * Класс заказа для API Stellar Burgers.
 *
 * @author Irina_Zakirova
 */
public class Order {

    /**
     * Список идентификаторов ингредиентов.
     */
    private List<String> ingredients;

    /**
     * Конструктор по умолчанию.
     */
    public Order() {
    }

    /**
     * Конструктор со списком ингредиентов.
     *
     * @param ingredients список идентификаторов ингредиентов.
     */
    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Возвращает список идентификаторов ингредиентов.
     *
     * @return список ингредиентов.
     */
    public List<String> getIngredients() {
        return ingredients;
    }

    /**
     * Устанавливает список идентификаторов ингредиентов.
     *
     * @param ingredients новый список ингредиентов.
     */
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}