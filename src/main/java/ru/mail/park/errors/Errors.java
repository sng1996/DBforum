package ru.mail.park.errors;

/**
 * Created by sergeigavrilko on 12.01.17.
 */
public class Errors {

    public static final int OK = 0;
    public static final int OBJECT_NOT_FOUND = 1;
    public static final int INVALID_REQUEST = 2;
    public static final int INCORRECT_REQUEST = 3;
    public static final int UNKNOWN_ERROR = 4;
    public static final int USER_EXISTS = 5;


    public static String getJson(int error) {
        return "{\"code\":" + error + ",\"response\": \"error\"}";
    }

}
