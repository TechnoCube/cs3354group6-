package com.alamkanak.weekview.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.alamkanak.weekview.sample.MainActivity;
import com.alamkanak.weekview.sample.R;

import java.util.Calendar;

/**
 * Alarm receiver class.
 * Handles reminders.
 * Created by Parker Franklin on 12/6/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private int startHour;
    private int startMinute;
    private String eventTitle;
    private Context mContext;

    /**
     * Overridden onReceive method.
     * @param context: context object
     * @param intent: Intent containing start time and title
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        startHour = intent.getIntExtra("hour", 0);
        startMinute = intent.getIntExtra("min", 0);
        eventTitle = intent.getStringExtra("title");
        mContext = context;


        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(0, createReminderNotif());
    }

    public Notification createReminderNotif() {
        // Set up notification appearance
        NotificationCompat.Builder notificBuilder = new
                NotificationCompat.Builder(mContext)
                .setContentTitle("Event Reminder")
                .setContentText(eventTitle + " at " + startHour + ":" + startMinute)
                .setTicker("Event is approaching!")
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        // Opens the app if the notification is clicked
        Intent notIntent = new Intent (mContext, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificBuilder.setContentIntent(contentIntent);

        return notificBuilder.build();
    }
}
