package ru.gdcn;

/*
Класс для упаковки и распаковки сообщений.
 */

import org.jboss.netty.channel.Channel;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServerMessage {

    private static String className = "ServerMessage";

    public static void read(String message, Channel userChannel) {
        Logger.log("Читаю сообщение...", className);
        JSONObject incomingMessage = (JSONObject) JSONValue.parse(message);
        String type = incomingMessage.get("type").toString();
        switch (type) {
            case "loginAttempt":
                Logger.log("Получил попытку логина.", className);
                //TODO обработать сообщение
                break;
            case "message":
                Logger.log("Получил обычное сообщение.", className);
                //TODO обработать сообщение
                break;
            case "disconnect":
                Logger.log("Получил сообщение об отключении.", className);
                //TODO обработать сообщение
                break;
            case "ping":
                Logger.log("Получил пинг-запрос.", className);
                //TODO обработать сообщение
                break;
            default:
                Logger.logError("Неизвестный тип сообщения: " + type, className);
                break;
        }
    }

    //TODO методы упаковки сообщений
}
