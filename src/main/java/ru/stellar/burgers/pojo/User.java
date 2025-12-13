package ru.stellar.burgers.pojo;

/**
 * Класс пользователя для API Stellar Burgers.
 *
 * @author Irina_Zakirova
 */
public class User {

    /**
     * Email пользователя.
     */
    private String email;

    /**
     * Пароль пользователя.
     */
    private String password;

    /**
     * Имя пользователя.
     *
     */
    private String name;

    /**
     * Конструктор по умолчанию.
     */
    public User() {
    }

    /**
     * Конструктор со всеми полями.
     *
     * @param email    email пользователя
     * @param password пароль пользователя
     * @param name     имя пользователя
     */
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email новый email пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password новый пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name новое имя пользователя
     */
    public void setName(String name) {
        this.name = name;
    }
}