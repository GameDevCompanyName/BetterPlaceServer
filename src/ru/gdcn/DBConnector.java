package ru.gdcn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
Класс для работы с БД
 */
public class DBConnector {

    private static String className;
    private static Connection connection;

    public static void initDBConnector(){
        className = "DBConnector";
        connection = initConnection();
        checkIfTableExists();
    }

    //Уствновка соединения с БД
    private static Connection initConnection(){
        Logger.log("Инициализация Connection.", className);
        String url = "jdbc:sqlite:BetterPlace.db";
        Connection connection = null;

        try{
            Logger.log("DriverManager...", className);
            connection = DriverManager.getConnection(url);
            Logger.log("DriverManager CONNECTED.", className);
        } catch (SQLException e){
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
                + " color VARCHAR(6) NOT NULL,\n"
                + " regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ");";

        try(Statement statement = connection.createStatement()){
            Logger.log("Statement execute...", className);
            statement.execute(sql);
            Logger.log("Statement executed.", className);
            Logger.log("Таблица пользователей существует и работает.", className);
        } catch (SQLException e){
            Logger.logError("Проблема доступа к таблице пользователей.", className);
            Logger.logError(e.toString(), className);
        }
    }
}