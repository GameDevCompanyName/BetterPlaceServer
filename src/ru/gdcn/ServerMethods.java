package ru.gdcn;

import org.jboss.netty.channel.Channel;

public class ServerMethods {

    private static String className = "ServerMethods";

    private static void sendMessage(Channel userChannel, String message) {
        Logger.log("Пишу сообщение в канал: " + message, className);
        userChannel.write(message);
    }

    public static void loginAttemptReceived(Channel userChannel, String login, String password){
        Logger.log("Обрабатываю попытку залогиниться по логину: " + login, className);
        boolean userOnline = Broadcaster.checkIfUserOnline(login);

        if (userOnline){
            Logger.log("Юзер с таким именем уже онлайн: " + login, className);
            sendUserAlreadyOnlineMessage(userChannel);
            return;
        }

        boolean userExists = DBConnector.searchForUser(login);

        if (userExists){
            Logger.log("Такой пользователь уже есть в базеб првоеряю пароль для: " + login, className);
            boolean passswordIsCorrect = DBConnector.checkLoginAttempt(login, password);
            if (passswordIsCorrect){
                Logger.log("Верный пароль для: " + login, className);
                User newUser = initUser(userChannel, login);
                sendUserLoginSuccessMessage(userChannel);
                sendColorToUser(userChannel, login, newUser.getColor());
                Broadcaster.userLoggedIn(userChannel, newUser);
            } else {
                Logger.log("Неверный пароль для: " + login, className);
                sendWrongPasswordMessage(userChannel);
            }
        }

        if (!userExists){
            Logger.log("Такого пользователя в базе ещё нет, создаю нового для: " + login, className);
            DBConnector.insertNewUser(login, password);
            Logger.log("Пользователь создан: " + login, className);
            User newUser = initUser(userChannel, login);
            sendUserLoginSuccessMessage(userChannel);
            sendColorToUser(userChannel, login, newUser.getColor());
            Broadcaster.userLoggedIn(userChannel, newUser);
        }
    }

    private static User initUser(Channel userChannel, String login){
        Logger.log("Инициализирую нового пользователя: " + login, className);
        String userColor = DBConnector.getUserColor(login);
        User newUser = new User(userChannel, login, userColor);
        Logger.log("Пользователь проинициализирован.", className);
        return newUser;
    }

    public static void messageReceived(Channel userChannel, String text) {
        Logger.log("Проверяю залогинен ли канал, пытающийся отправить сообщение.", className);
        boolean userIsLogged = Broadcaster.checkIfChannelLogged(userChannel);
        if (userIsLogged){
            Logger.log("Канал залогинен, передаю сообщение в Broadcaster.", className);
            Broadcaster.messageRecieved(userChannel, text);
        } else {
            Logger.log("Канал НЕ залогинен и не может отправлять сообщения.", className);
        }
    }

    public static void pingReceived(Channel userChannel) {
        Logger.log("Пришёл запрос пинга.", className);
        sendPongAnswer(userChannel);
    }

    public static void disconnectReceived(Channel userChannel) {
        Logger.log("Канал отключается.", className);
        Broadcaster.userDisconnected(userChannel);
        userChannel.close();
        Logger.log("Сообщение об отключении обработано.", className);
    }

    private static void sendUserAlreadyOnlineMessage(Channel userChannel) {
        Logger.log("Отправляю сообщение о том, что такой пользователь уже залогинен.", className);
        sendMessage(userChannel, ServerMessage.loginAlreadyError());
    }

    private static void sendUserLoginSuccessMessage(Channel userChannel) {
        Logger.log("Отправляю сообщение об удачном логине.", className);
        sendMessage(userChannel, ServerMessage.loginSuccess());
    }

    private static void sendWrongPasswordMessage(Channel userChannel) {
        Logger.log("Отправляю сообщение о неверном пароле.", className);
        sendMessage(userChannel, ServerMessage.loginWrongError());
    }

    private static void sendColorToUser(Channel userChannel, String login, String color) {
        Logger.log("Отправляю пользователю его цвет.", className);
        try {
            userChannel.write(ServerMessage.userColor(login, color)).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendPongAnswer(Channel userChannel) {
        Logger.log("Отправляю ответ на эхо-запрос.", className);
        sendMessage(userChannel, ServerMessage.serverPong());
    }
}
