package ru.gdcn;

public class Utilites {

    public static String getStartText(String className){
        String space = "";
        for (int i = 0; i < 13 - className.length(); i++){
            space = space.concat(" ");
        }
        return "[" + className + "]:" + space + " ";
    }
}
