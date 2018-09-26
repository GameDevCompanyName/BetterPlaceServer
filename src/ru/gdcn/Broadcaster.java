package ru.gdcn;

import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class Broadcaster {

    private static String className = "Broadcaster";

    private static Map<Channel, User> loggedChannels = new HashMap<>();
    private static Map<User, Channel> loggedUsers = new HashMap<>();

    public static boolean checkIfUserOnline(String login) {
        boolean userOnline = false;
        User userToCheck = new User(login);

        Logger.log("Проверяю залогинен ли пользователь: " + login, className);
        if (loggedUsers.containsKey(userToCheck))
            userOnline = true;
        Logger.log("Проверил онлайн ли пользователь: " + login, className);

        return userOnline;
    }

    public static void userLoggedIn(Channel userChannel, User newUser) {
        Logger.log("Добавляю нового пользователя в список залогиненых: " + newUser.getLogin(), className);
        loggedChannels.put(userChannel, newUser);
        loggedUsers.put(newUser, userChannel);
        sendAllMessage(ServerMessage.serverMessage(
                TextFormer.userConnected(newUser.getLogin())
        ));
    }

    public static boolean checkIfChannelLogged(Channel userChannel) {
        boolean channelLogged = false;

        Logger.log("Проверяю залогинен ли канал.", className);
        if (loggedChannels.containsKey(userChannel))
            channelLogged = true;

        return channelLogged;
    }

    public static void messageRecieved(Channel userChannel, String message) {
        User sender = loggedChannels.get(userChannel);
        Logger.log("Отправляю всем пользователям сообщение пользователя.", className);
        sendAllMessage(ServerMessage.userMessage(
                sender.getLogin(),
                message,
                sender.getColor()
        ));
    }

    public static void userDisconnected(Channel userChannel) {
        boolean channelOnline = checkIfChannelLogged(userChannel);

        if (!channelOnline)
            return;
        User userToDelete = loggedChannels.get(userChannel);
        Logger.log("Удаляю пользователя из списка залогиненых: " + userToDelete.getLogin(), className);
        loggedChannels.remove(userChannel);
        loggedUsers.remove(userToDelete);
        sendAllMessage(ServerMessage.serverMessage(
                TextFormer.userDisconnected(userToDelete.getLogin())
        ));
    }

    private static void sendAllMessage(String JSONmessage) {
        for (User user : loggedChannels.values()) {
            user.sendMessage(JSONmessage);
        }
    }
}
