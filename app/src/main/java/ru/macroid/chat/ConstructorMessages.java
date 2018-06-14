package ru.macroid.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConstructorMessages {
    String authors, messages, times;

    Calendar calendar;
    SimpleDateFormat format;
    String time;
    String batteryLevel;

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

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = time;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

}
