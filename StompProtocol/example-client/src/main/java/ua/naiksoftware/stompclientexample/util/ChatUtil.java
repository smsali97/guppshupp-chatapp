package ua.naiksoftware.stompclientexample.util;

import ua.naiksoftware.stomp.StompClient;

public class ChatUtil {

    public static StompClient mStompClient;

    public static boolean isConnected = false;

    public static final String ANDROID_EMULATOR_LOCALHOST = "10.0.2.2";
    public static final String SERVER_PORT = "8080";

    public static String currentUsername = "smsali97";
}
