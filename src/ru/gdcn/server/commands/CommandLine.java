package ru.gdcn.server.commands;

import ru.gdcn.server.utilities.Logger;
import ru.gdcn.server.ServerMethods;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLine extends Thread {

    private static String className = "CommandLine";

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("^/[a-zA-Z0-9\\s]+$"); //Регулярное выражение для команд
        Matcher m;
        String message;
        while (true){
            message = scanner.nextLine();
            m = pattern.matcher(message);
            //Если команда, то пытаемся вызвать ее
            if(m.matches()) {
                Logger.log("Получил команду от сервера: " + message, className);
                message = message.substring(1);
                String[] command = message.split(" ");
                Commands.executeServerCommand(command);
            }
            //Иначе отправляем как сообщение в чат
            else {
                ServerMethods.sendServerMessageAll(message);
                System.out.println(" SERVER: " + message);
            }
        }
    }
}
