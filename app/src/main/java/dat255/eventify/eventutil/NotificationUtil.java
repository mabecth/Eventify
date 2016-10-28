package dat255.eventify.eventutil;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;

import java.util.concurrent.TimeUnit;

import dat255.eventify.R;
import dat255.eventify.activity.DetailActivity;
import dat255.eventify.manager.StorageManager;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationUtil {
    private long oneHourInMS = 3600000;

    //calls the neccesarry methods to create a notification
    public void createNotification() {
        long delay = calculateSettingsDelay();
        long hoursToEvent = (int) TimeUnit.MILLISECONDS.toHours(delay);
        delay = delayToNotification(delay);
        scheduleNotification(StorageManager.getInstance().getFavorites().get(0).getTitle(), "You have an event in " + hoursToEvent + "h", delay);
    }

    //scheduler for notifcation so it triggers at the right time
    public void scheduleNotification(String title, String content, long delay) {

        //Compare to code in getNotification, may need cleaning moving
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, getNotification(title, content));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    //creates the actual notification
    public Notification getNotification(String title, String content) {

        //May need cleaning, makes the notification open the detailview
        Intent resultIntent = new Intent(getApplicationContext(), DetailActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(DetailActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //builds the notification
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.logo_android)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(resultPendingIntent);
        return builder.build();
    }

    //calculating delay from settings
    public long calculateSettingsDelay() {
        long delay = 0;
        switch (StorageManager.getInstance().getSettings().get("notifyHour")) {
            case 0:
                break;
            case 1:
                delay = oneHourInMS * 2;
                break;
            case 2:
                delay = oneHourInMS * 4;
                break;
            case 3:
                delay = oneHourInMS * 6;
                break;
            case 4:
                delay = oneHourInMS * 8;
                break;
            case 5:
                delay = oneHourInMS * 10;
                break;
            case 6:
                delay = oneHourInMS * 12;
                break;
        }
        switch (StorageManager.getInstance().getSettings().get("notifyDay")) {
            case 0:
                break;
            case 1:
                delay +=oneHourInMS * 24;
                break;
            case 2:
                delay +=oneHourInMS * 48;
                break;
            case 3:
                delay +=oneHourInMS * 72;
                break;
            case 4:
                delay +=oneHourInMS * 96;
                break;
        }

        return delay;
    }

    //converts delay time so notification will trigger on time. Also sets notifications to start of event if the set notification time in settings would be earlier than today.
    public long delayToNotification(long delay) {
        long calculatedDelay = StorageManager.getInstance().getFavorites().get(0).getEventTimeInMillis() - delay;

        calculatedDelay = System.currentTimeMillis() - calculatedDelay;

        if (delay > calculatedDelay) {
            return StorageManager.getInstance().getFavorites().get(0).getEventTimeInMillis() - System.currentTimeMillis();
        } else {
            return calculatedDelay;
        }
    }

    //fix so notifications can be canceled
    /*
    public void cancelNotification() {
        Intent intent = new Intent(getApplicationContext(),NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }*/
}
