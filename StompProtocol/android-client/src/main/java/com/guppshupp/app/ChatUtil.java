package com.guppshupp.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ua.naiksoftware.stomp.StompClient;

public class ChatUtil {

    public static StompClient mStompClient;

    public static boolean isConnected = false;

    public static final String ANDROID_EMULATOR_LOCALHOST = "guppshupp.eu-central-1.elasticbeanstalk.com";
//    public static final String ANDROID_EMULATOR_LOCALHOST = "10.0.2.2";

    public static final String SERVER_PORT = "8080";

    public static String currentUsername = "smsali97";
    static SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");


    public static String getCurrentTimeinKarachi(String currTime) {

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(df.parse(currTime));
        } catch (ParseException e) {
            calendar.setTime(new Date());
        }
        calendar.add(Calendar.HOUR_OF_DAY, 5);
        return df.format(calendar.getTime());
    }
}
