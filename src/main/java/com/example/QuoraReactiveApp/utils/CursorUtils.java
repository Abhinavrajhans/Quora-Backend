package com.example.QuoraReactiveApp.utils;

import java.time.LocalDateTime;

public class CursorUtils {

    public static boolean isValidCursor(String cursor){
        if(cursor==null || cursor.isEmpty()){
            return false;
        }
        try{
            LocalDateTime.parse(cursor); // if the string given to us is parsable in localddtetime then we can say yes it is a valid cursor.
            return true;
        }
        catch(Exception e){
            return false;
        }
    }


    public static LocalDateTime parseCursor(String cursor){
        if(!isValidCursor(cursor)){
            throw new IllegalArgumentException("Cursor Not Valid");
        }
        return LocalDateTime.parse(cursor);
    }
}
