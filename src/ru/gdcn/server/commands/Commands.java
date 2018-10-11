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
            case "kick":
                if (commands.length == 2)
                    ServerMethods.kickUser(commands[1]);
                else
                    print("Неверная команда.");
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
        if (commands.length == 1)
            ServerMethods.sendMessageUser(
                    userChannel,
                    ServerMessage.serverMessage("Нет такой команды.")
            );
        switch (commands[1]) {
            case "clients":
                for (User user : Broadcaster.getUsers())
                    ServerMethods.sendMessageUser(
                            userChannel,
                            ServerMessage.serverMessage(user.getLogin() + "\n"));
                break;
            case "echo":
                ServerMethods.echoReceived(userChannel, commands);
                break;
            case "help":
                ServerMethods.sendMessageUser(
                        userChannel,
                        ServerMessage.serverMessage("Доcтупные команды:\n" +
                                "/server clients\n" +
                                "/server echo <your text>\n" +
                                "/server help")
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
