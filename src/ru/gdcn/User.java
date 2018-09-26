package ru.gdcn;

import org.jboss.netty.channel.Channel;

public class User {

    private static String className = "User";
    private String login;
    private Channel userChannel;
    private String color;

    public User(Channel userChannel, String login, String color){
        Logger.log("Создаю нового юзера: " + login, className);
        this.userChannel = userChannel;
        this.login = login;
        this.color = color;
    }

    public User(String login){
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public Channel getUserChannel() {
        return userChannel;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void sendMessage(String message){
        userChannel.write(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return login != null ? login.equals(user.login) : user.login == null;
    }

    @Override
    public int hashCode() {
        return login != null ? login.hashCode() : 0;
    }
}
