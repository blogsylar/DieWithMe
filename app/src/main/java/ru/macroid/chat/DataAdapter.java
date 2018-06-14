package ru.macroid.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder>  {

    ArrayList<ConstructorMessages> messages;
    LayoutInflater inflater;


    public DataAdapter(Context context, ArrayList<ConstructorMessages> messages) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConstructorMessages msg = messages.get(position);
        holder.authorItem.setText(msg.authors);
        holder.messageItem.setText(msg.messages);

        holder.timeItem.setText(msg.times);
        holder.batteryItem.setText(msg.batteryLevel);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
