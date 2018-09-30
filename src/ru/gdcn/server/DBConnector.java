package ru.gdcn.server;

import ru.gdcn.server.utilities.Logger;

import java.sql.*;
import java.util.Random;

/*
Класс для работы с БД
 */
public class DBConnector {

    private static String className;
    private static Connection connection;

    public static void initDBConnector() {
        className = "DBConnector";
        connection = initConnection();
        checkIfTableExists();
    }

    //Уствновка соединения с БД
    private static Connection initConnection() {
        Logger.log("Инициализация Connection.", className);
        String url = "jdbc:sqlite:BetterPlace.db";
        Connection connection = null;

        try {
            Logger.log("DriverManager...", className);
            connection = DriverManager.getConnection(url);
            Logger.log("DriverManager CONNECTED.", className);
        } catch (SQLException e) {
            Logger.logError("DriverManager NOT CONNECTED.", className);
            Logger.logError(e.toString(), className);
        }

        return connection;
    }

    //Проверка наличия таблицы пользователей
    private static void checkIfTableExists() {
        Logger.log("Проверяю наличие таблицы пользователей...", className);

        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + " id INTEGER PRIMARY KEY,\n"
                + " login VARCHAR(20) NOT NULL UNIQUE,\n"
                + " password VARCHAR(20) NOT NULL,\n"
                + " color VARCHAR(7) NOT NULL,\n"
                + " regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ");";

        try (Statement statement = connection.createStatement()) {
            Logger.log("Statement execute...", className);
            statement.execute(sql);
            Logger.log("Statement executed.", className);
            Logger.log("Таблица пользователей существует и работает.", className);
        } catch (SQLException e) {
            Logger.logError("Проблема доступа к таблице пользователей.", className);
            Logger.logError(e.toString(), className);
        }
    }

    //Проверка логина и пароля
    public static boolean checkLoginAttempt(String login, String password) {
        Logger.log("Поверяю правильность пароля юзера: " + login, className);
        String sql = "SELECT login, password FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String dbPassword = rs.getString("password");
                if (password.equals(dbPassword)) {
                    Logger.log("Пароль правильный для юзера: " + login, className);
                    return true;
                } else {
                    Logger.log("Пароль неверный для юзера: " + login, className);
                    return false;
                }
            }
        } catch (SQLException e) {
            Logger.logError("Что-то пошло не так при проверке пароля для юзера: " + login, className);
            Logger.logError(e.toString(), className);
        }
        return false;
    }

    //Добавление нового пользователя
    public static void insertNewUser(String login, String password) {
        String sql = "INSERT INTO Users (login, password, color) VALUES(?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            //TODO Сделать генератор HEX для цвета
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('#');
            stringBuilder.append(1 + (new Random().nextInt(9)));
            stringBuilder.append(1 + (new Random().nextInt(9)));
            stringBuilder.append(1 + (new Random().nextInt(9)));
            stringBuilder.append(1 + (new Random().nextInt(9)));
            stringBuilder.append(1 + (new Random().nextInt(9)));
            stringBuilder.append(1 + (new Random().nextInt(9)));

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, stringBuilder.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Проверка наличия юзера
    public static boolean searchForUser(String login) {
        String sql = "SELECT * FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Получить цвет пользователя
    public static String getUserColor(String login) {
        String sql = "SELECT color FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                return "false";
            }
            return rs.getString("color");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "false";
    }
}