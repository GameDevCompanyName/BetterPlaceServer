package ru.gdcn.server;

import org.jboss.netty.channel.Channel;
import ru.gdcn.server.commands.Commands;
import ru.gdcn.server.utilities.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerMethods {

    private static String className = "ServerMethods";

    public static void loginAttemptReceived(Channel userChannel, String login, String password) {
        Logger.log("Обрабатываю попытку залогиниться по логину: " + login, className);

        boolean userOnline = Broadcaster.checkIfUserOnline(login);
        if (userOnline) {
            Logger.log("Юзер с таким именем уже онлайн: " + login, className);
            sendMessageUserAlreadyOnline(userChannel);
            return;
        }

        boolean userExists = DBConnector.searchForUser(login);
        if (userExists) {
            Logger.log("Такой пользователь уже есть в базе првоеряю пароль для: " + login, className);
            boolean passswordIsCorrect = DBConnector.checkLoginAttempt(login, password);
            if (passswordIsCorrect) {
                Logger.log("Верный пароль для: " + login, className);
                User newUser = initUser(userChannel, login);
                sendMesageUserLoginSuccess(userChannel);
                sendMessageUserColor(userChannel, login, newUser.getColor());
                Broadcaster.userLoggedIn(userChannel, newUser);
            } else {
                Logger.log("Неверный пароль для: " + login, className);
                sendMesssageUserWrongPassword(userChannel);
            }
        }

        if (!userExists) {
            Logger.log("Такого пользователя в базе ещё нет, создаю нового для: " + login, className);
            DBConnector.insertNewUser(login, password);
            Logger.log("Пользователь создан: " + login, className);
            User newUser = initUser(userChannel, login);
            sendMesageUserLoginSuccess(userChannel);
            sendMessageUserColor(userChannel, login, newUser.getColor());
            Broadcaster.userLoggedIn(userChannel, newUser);
        }
    }

    public static void messageReceived(Channel userChannel, String text) {
        Logger.log("Проверяю залогинен ли канал, пытающийся отправить сообщение.", className);
        boolean userIsLogged = Broadcaster.checkIfChannelLogged(userChannel);
        if (userIsLogged) {
            Logger.log("Канал залогинен, передаю сообщение в Broadcaster.", className);
            Broadcaster.messageBroadcast(userChannel, text);
        } else {
            Logger.log("Канал НЕ залогинен и не может отправлять сообщения.", className);
        }
    }

    public static void pingReceived(Channel userChannel) {
        Logger.log("Пришёл запрос пинга.", className);
        sendMessageUserPong(userChannel);
    }

    public static void disconnectReceived(Channel userChannel) {
        Logger.log("Канал отключается.", className);
        Broadcaster.userDisconnected(userChannel);
        userChannel.close();
        Logger.log("Сообщение об отключении обработано.", className);
    }

    public static void commandReceived(Channel userChannel, String text) {
        Logger.log("Проверяю залогинен ли канал, пытающийся выполнить команду.", className);
        boolean userIsLogged = Broadcaster.checkIfChannelLogged(userChannel);
        if (userIsLogged) {
            Logger.log("Канал залогинен, передаю команду в Commands.", className);
            Pattern pattern = Pattern.compile("^/[a-zA-Z0-9\\s]+$");
            Matcher m = pattern.matcher(text);
            if (m.matches()) {
                Logger.log("Команда корректна.", className);
                text = text.substring(1);
                String[] commands = text.split(" ");
                Commands.executeUserCommand(userChannel, commands);
            } else {
                Logger.log("Команда некорректна.", className);
                sendMessageUser(userChannel, ServerMessage.serverMessage("Некорретная команда."));
            }
        } else {
            Logger.log("Канал НЕ залогинен и не может выполнять команды.", className);
        }
    }

    public static void echoReceived(Channel userChannel, String[] commands) {
        Logger.log("Отправляю эхо-запрос.", className);
        if (commands.length >= 3) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 2; i < commands.length - 1; i++)
                stringBuilder.append(commands[i]).append(" ");
            stringBuilder.append(commands[commands.length-1]);
            ServerMethods.sendMessageUser(userChannel,
                    ServerMessage.serverMessage(stringBuilder.toString()));
        } else {
            ServerMethods.sendMessageUser(userChannel,
                    ServerMessage.serverMessage("Hello, World!"));
        }
    }

    public static void kickUser(String login) {
        Broadcaster.userKicked(login);
        sendServerMessageAll(login + " был исключен.");
    }

    public static void sendServerMessageAll(String text) {
        Logger.log("Отправляю сервеное сообщение.", className);
        Broadcaster.serverMessageBroadcast(text);
    }

    public static void sendMessageUser(Channel userChannel, String message) {
        Logger.log("Пишу сообщение в канал: " + message, className);
        userChannel.write(message);
    }

    private static User initUser(Channel userChannel, String login) {
        Logger.log("Инициализирую нового пользователя: " + login, className);
        String userColor = DBConnector.getUserColor(login);
        User newUser = new User(userChannel, login, userColor);
        Logger.log("Пользователь проинициализирован.", className);
        return newUser;
    }

    private static void sendMessageUserAlreadyOnline(Channel userChannel) {
        Logger.log("Отправляю сообщение о том, что такой пользователь уже залогинен.", className);
        sendMessageUser(userChannel, ServerMessage.loginAlreadyError());
    }

    private static void sendMesageUserLoginSuccess(Channel userChannel) {
        Logger.log("Отправляю сообщение об удачном логине.", className);
        sendMessageUser(userChannel, ServerMessage.loginSuccess());
    }

    private static void sendMesssageUserWrongPassword(Channel userChannel) {
        Logger.log("Отправляю сообщение о неверном пароле.", className);
        sendMessageUser(userChannel, ServerMessage.loginWrongError());
    }

    private static void sendMessageUserColor(Channel userChannel, String login, String color) {
        Logger.log("Отправляю пользователю его цвет.", className);
        sendMessageUser(userChannel, ServerMessage.userColor(login, color));
    }

    private static void sendMessageUserPong(Channel userChannel) {
        Logger.log("Отправляю ответ на эхо-запрос.", className);
        sendMessageUser(userChannel, ServerMessage.serverPong());
    }
}
