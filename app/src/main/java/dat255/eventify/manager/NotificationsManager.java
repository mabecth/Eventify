package dat255.eventify.manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import dat255.eventify.R;
import dat255.eventify.util.NotificationsUtil;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Johannes on 2016-10-25.
 */

public class NotificationsManager {
    public void createNotification(){
        long hoursToEvent = (int) TimeUnit.MILLISECONDS.toMinutes(StorageManager.getInstance().getFavorites().get(0).getEventTimeInMillis()-System.currentTimeMillis())/ 60;
        long hoursToNotification = hoursToEvent;
        long hours = StorageManager.getInstance().getSettings().get("notifyHour");

        //long delayToNotification = (int) TimeUnit.MILLISECONDS.toMinutes()
        scheduleNotification("You have an event in "+hoursToEvent+"h",50000);
    }

    public void scheduleNotification(String content, int delay) {


        Intent notificationIntent = new Intent(getApplicationContext(), NotificationsUtil.class);
        notificationIntent.putExtra(NotificationsUtil.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationsUtil.NOTIFICATION, getNotification(content));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.logo_android);
        return builder.build();
    }
}
