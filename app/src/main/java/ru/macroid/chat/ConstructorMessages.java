package ru.macroid.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConstructorMessages {
    String authors, messages, times;

    Calendar calendar;
    SimpleDateFormat format;
    String time;
    String batteryLevel;

    String sideMessage;



    public ConstructorMessages(String authors, String messages, String times, String batteryLevel) {

        this.authors = authors;
        this.messages = messages;
        this.times = times;
        this.batteryLevel = batteryLevel;
    }

    public ConstructorMessages() {

    }



    public String getAuthors() {
        return authors;
    }


    public String getMessages() {
        return messages;
    }


    public String getTimes() {
        return times;
    }


    public String getBatteryLevel() {
        return batteryLevel;
    }

}
