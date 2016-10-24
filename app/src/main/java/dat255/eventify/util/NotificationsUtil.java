package dat255.eventify.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Random;

import dat255.eventify.R;
import dat255.eventify.activity.DetailActivity;
import dat255.eventify.activity.MainActivity;
import dat255.eventify.manager.StorageManager;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by Johannes on 2016-10-24.
 */

public class NotificationsUtil {
    private Random random = new Random();
    private int uniqueID = random.nextInt(9999 - 1000) + 1000;
    private NotificationManager notificationManager;
    private Notification notification;

    public void buildNotification(Context context, String title, String message){
        //unique id should be randomized for each notification

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // fix så right class is started when you press notification
        //Intent intent = new Intent(context,DetailActivity.class);
        Intent intent = new Intent(context,NotificationsUtil.class);


        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.logo_android)
        .setTicker("This is the ticker")
        .setWhen(System.currentTimeMillis())
        .setContentTitle(title)
        .setContentText(message)
                ;

        notification = builder.build();

        notificationManager.notify(uniqueID,notification);
    }

    public void setNotificationTime(Context context){
        Intent intent = new Intent(context,NotificationsUtil.class);


        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 23);
        calendar.set(Calendar.SECOND, 00);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20*1000 , pendingIntent);

        notificationManager.notify(uniqueID,notification);

/*NotificationsUtil notificationsUtil = new NotificationsUtil();
                    notificationsUtil.buildNotification(getApplicationContext(),"mamma snälla","skjut mig");*/
    }
}
