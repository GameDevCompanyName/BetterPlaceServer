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
                ServerMethods.loginAttemptReceived(
                        userChannel,
                        incomingMessage.get("login").toString(),
                        incomingMessage.get("password").toString()
                );
                break;
            case "message":
                Logger.log("Получил обычное сообщение.", className);
                ServerMethods.messageReceived(
                        userChannel,
                        incomingMessage.get("text").toString()
                );
                break;
            case "disconnect":
                Logger.log("Получил сообщение об отключении.", className);
                ServerMethods.disconnectReceived(userChannel);
                break;
            case "ping":
                Logger.log("Получил пинг-запрос.", className);
                ServerMethods.pingReceived(userChannel);
                break;
            default:
                Logger.logError("Неизвестный тип сообщения: " + type, className);
                break;
        }
    }

    public static String loginSuccess(){
        JSONObject object = new JSONObject();
        object.put("type", "loginSuccess");
        return object.toJSONString();
    }

    public static String loginWrongError(){
        JSONObject object = new JSONObject();
        object.put("type", "loginWrongError");
        return object.toJSONString();
    }

    public static String loginAlreadyError(){
        JSONObject object = new JSONObject();
        object.put("type", "loginAlreadyError");
        return object.toJSONString();
    }

    public static String userColor(String login, String color){
        JSONObject object = new JSONObject();
        object.put("type", "userColor");
        object.put("login", login);
        object.put("color", color);
        return object.toJSONString();
    }

    public static String userMessage(String login, String message, String color){
        JSONObject object = new JSONObject();
        object.put("type", "userMessage");
        object.put("login", login);
        object.put("text", message);
        object.put("color", color);
        return object.toJSONString();
    }

    public static String serverMessage(String message){
        JSONObject object = new JSONObject();
        object.put("type", "serverMessage");
        object.put("text", message);
        return object.toJSONString();
    }

    public static String serverPong(){
        JSONObject object = new JSONObject();
        object.put("type", "pong");
        return object.toJSONString();
    }
}
