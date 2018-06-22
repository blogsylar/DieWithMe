package ru.macroid.chat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private IntentFilter intentFilter;
    private Intent batteryStatus;
    private int battery;
    private Timer timer;
    private TimerTask timerTask;
    private int a = 0;
    private int REQUEST_CODE = 777;
    private NotificationManagerCompat manager;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        manager = NotificationManagerCompat.from(this);
        timer = new Timer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                batteryStatus = registerReceiver(null, intentFilter);
                battery = batteryStatus.getIntExtra("level", -1);

                if (a == 0 && battery <= 15) {

                    notifyMe();
                    a = 1;
                }
            }
        };

        timer.schedule(timerTask, 1000, 5000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void notifyMe() {

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        style.
                setBigContentTitle(getString(R.string.return_in_chat)).
                setSummaryText(getString(R.string.notify_battery) + battery + "%");

        builder.
                setSmallIcon(android.R.drawable.btn_star).
                setStyle(style).
                setPriority(Notification.PRIORITY_MAX).
                setSound(Uri.parse("android.resourse://" + getPackageName() + "/" + R.raw.bikeringbell)).
                setContentIntent(pIntent).
                setAutoCancel(true).
                setVibrate(new long[] {100, 100, 100}).
                setLights(Color.GREEN, 1000, 500);

        manager.notify(R.id.BIG_PICTURE_NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        a = 0;
    }
}
