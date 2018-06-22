package ru.macroid.chat;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Stop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MyService.class));
        System.runFinalization();
        System.exit(0);
    }
}
