package ru.gdcn.server.commands;

import org.jboss.netty.channel.Channel;
import ru.gdcn.server.Broadcaster;
import ru.gdcn.server.ServerMessage;
import ru.gdcn.server.ServerMethods;
import ru.gdcn.server.User;

public class Commands {

    private static String className = "Commands";

     static void executeServerCommand(String[] commands) {
        switch (commands[0]) {
            case "clients":
                for (User user : Broadcaster.getUsers())
                    print(user.getLogin());
                break;
            case "shutdown":
                print("Сервер остановил свою работу.");
                System.exit(0);
                break;
            default:
                print("Нет такой команды.");
                break;
        }
    }

    public static void executeUserCommand(Channel userChannel, String[] commands) {
        switch (commands[1]) {
            case "clients":
                for (User user : Broadcaster.getUsers())
                    ServerMethods.sendMessageUser(
                            userChannel,
                            ServerMessage.serverMessage(user.getLogin() + "\n"));
                    break;
            case "info":
                ServerMethods.sendMessageUser(
                        userChannel,
                        ServerMessage.serverMessage("Доcтупные команды:\n/server clients\n/server info")
                );
                break;
            default:
                ServerMethods.sendMessageUser(
                        userChannel,
                        ServerMessage.serverMessage("Нет такой команды.")
                );
                break;
        }
    }

    private static void print(String text) {
        System.out.println(text);
    }

}
