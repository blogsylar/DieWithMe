package ru.macroid.chat;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * MainActivityVariables
     */

    Button btsendMessage;
    EditText etMessageInput;
    RecyclerView messagesRecycler;

    FirebaseDatabase database;
    DatabaseReference myRef;

    Calendar calendar;
    SimpleDateFormat format;
    String time;
    String sendMessage;

    ConstructorMessages constructorPushedMessage;
    ConstructorMessages receivedMessagesFromFirebase;
    ArrayList<ConstructorMessages> constructorMessages = new ArrayList<ConstructorMessages>();

    int SIGN_IN_REQUEST_CODE = 1;

    DataAdapter dataAdapter;
    Iterator i;
    String nameReceivedFromFirebase, messageReceiverFromFirebase, timeReceivedFromFirebase, batterReceivedFromFirebase;

    public static int MAX_MESSAGE_LENGTH = 150;
    String authors;

    IntentFilter intentFilter;
    Intent batteryStatus;
    int battery;
    String batteryLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = registerReceiver(null, intentFilter);
        battery = batteryStatus.getIntExtra("level", -1);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );

        } else {

            Toast.makeText(this,"Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Toast.LENGTH_LONG).show();
        }

        etMessageInput = (EditText) findViewById(R.id.etMessageInput);
        btsendMessage = (Button) findViewById(R.id.btsendMessage);
        btsendMessage.setOnClickListener(this);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {

                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();

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
                }

                constructorPushedMessage = new ConstructorMessages(authors, sendMessage, time, batteryLevel);

                myRef.push()
                        .setValue(new ConstructorMessages(
                                constructorPushedMessage.getAuthors(),
                                constructorPushedMessage.getMessages(),
                                constructorPushedMessage.getTimes(),
                                constructorPushedMessage.getBatteryLevel()
                        ));

                //constructorMessages.add(constructorPushedMessage);

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

                            Toast.makeText(MainActivity.this,"You have been signed out.",Toast.LENGTH_LONG) .show();
                            finish();
                        }
                    });
        }
        return true;
    }
}
