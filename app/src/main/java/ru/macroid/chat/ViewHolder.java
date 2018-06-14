package ru.macroid.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewHolder extends RecyclerView.ViewHolder {


    TextView messageItem;
    TextView timeItem;
    TextView authorItem;
    TextView batteryItem;

    Calendar calendar;
    SimpleDateFormat format;
    DataAdapter time;

    public ViewHolder(View itemView) {
        super(itemView);

        authorItem = itemView.findViewById(R.id.authorItem);
        timeItem = itemView.findViewById(R.id.timeItem);
        messageItem = itemView.findViewById(R.id.messageItem);
        batteryItem = itemView.findViewById(R.id.batteryItem);

    }
}