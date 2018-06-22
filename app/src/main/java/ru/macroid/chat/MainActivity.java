package ru.macroid.chat;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btsendMessage;
    private EditText etMessageInput;
    private TextView batteryInToolbar;
    private RecyclerView messagesRecycler;
    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Calendar calendar;
    private SimpleDateFormat format;
    private String time;
    private String sendMessage;
    private ConstructorMessages constructorPushedMessage;
    private ConstructorMessages receivedMessagesFromFirebase;
    private ArrayList<ConstructorMessages> constructorMessages;
    private int SIGN_IN_REQUEST_CODE = 1;
    private DataAdapter dataAdapter;
    private Iterator i;
    private String nameReceivedFromFirebase, messageReceiverFromFirebase, timeReceivedFromFirebase, batterReceivedFromFirebase;
    private static int MAX_MESSAGE_LENGTH = 150;
    private String authors;
    private IntentFilter intentFilter;
    private Intent batteryStatus;
    private int battery;
    private String batteryLevel;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMessageInput = (EditText) findViewById(R.id.etMessageInput);
        batteryInToolbar = (TextView) findViewById(R.id.batteryInToolbar);
        btsendMessage = (ImageButton) findViewById(R.id.btsendMessage);
        btsendMessage.setOnClickListener(this);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                batteryStatus = registerReceiver(null, intentFilter);
                battery = batteryStatus.getIntExtra("level", -1);

                setBatteryInToolbar();

                Log.d("happy", "" + battery);
            }
        };

        timer.schedule(timerTask, 1000, 5000);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        constructorMessages = new ArrayList<ConstructorMessages>();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );

        } else {

            Toast.makeText(this,getString(R.string.welcome_sign) + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Toast.LENGTH_LONG).show();
        }

        messagesRecycler = findViewById(R.id.messagesRecycler);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));

        dataAdapter = new DataAdapter(this, constructorMessages);
        messagesRecycler.setAdapter(dataAdapter);

        myRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    nameReceivedFromFirebase = (String) ((DataSnapshot)i.next()).getValue();
                    messageReceiverFromFirebase = (String) ((DataSnapshot)i.next()).getValue();
                    timeReceivedFromFirebase = (String) ((DataSnapshot)i.next()).getValue();
                    batterReceivedFromFirebase = (String) ((DataSnapshot)i.next()).getValue();
                }

                receivedMessagesFromFirebase = new ConstructorMessages(nameReceivedFromFirebase, messageReceiverFromFirebase, timeReceivedFromFirebase, batterReceivedFromFirebase);
                constructorMessages.add(receivedMessagesFromFirebase);
                dataAdapter.notifyDataSetChanged();
                messagesRecycler.smoothScrollToPosition(constructorMessages.size());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setBatteryInToolbar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteryInToolbar.setText(battery + "%");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {

                Toast.makeText(this, R.string.welcome, Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, R.string.error_sign_in, Toast.LENGTH_LONG).show();

                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btsendMessage:

                calendar = Calendar.getInstance();
                format = new SimpleDateFormat("HH:mm");
                time = format.format(calendar.getTime());
                sendMessage = etMessageInput.getText().toString();
                authors = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                batteryLevel = String.valueOf(battery);

                if (sendMessage.equals("")) {

                    Toast.makeText(this, R.string.input_some_text, Toast.LENGTH_SHORT).show();
                    return;

                } else if (sendMessage.length() > MAX_MESSAGE_LENGTH) {

                    Toast.makeText(this, R.string.to_long_message, Toast.LENGTH_SHORT).show();
                    return;
                } else if (battery > 15) {
                    Toast.makeText(this, R.string.input_when_battery_over_15, Toast.LENGTH_SHORT).show();
                    return;
                }

                constructorPushedMessage = new ConstructorMessages(authors, sendMessage, time, batteryLevel);

                myRef.push()
                        .setValue(new ConstructorMessages(
                                constructorPushedMessage.getAuthors(),
                                constructorPushedMessage.getMessages(),
                                constructorPushedMessage.getTimes(),
                                constructorPushedMessage.getBatteryLevel()
                        ));

                etMessageInput.setText("");

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {

            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(MainActivity.this, R.string.you_sign_out,Toast.LENGTH_LONG) .show();
                    finish();
                }
            });
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MyService.class));
        System.runFinalization();
        System.exit(0);
        finish();
    }
}
