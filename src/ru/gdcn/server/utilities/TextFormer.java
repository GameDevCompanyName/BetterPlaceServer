package ru.gdcn.server.utilities;

public class TextFormer {
    public static String userConnected(String login) {
        return "Пользователь " + login + " зашёл в комнату";
    }

    public static String userDisconnected(String login) {
        return "Пользователь " + login + " покинул комнату";
    }
}
