package dat255.eventify.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import java.util.concurrent.TimeUnit;
import dat255.eventify.R;
import dat255.eventify.activity.DetailActivity;
import dat255.eventify.model.Event;

public class NotificationsUtil extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

    }
/*
    @Override
    public void onReceive(Context context, Intent intent) {
        Event e = (Event) intent.getSerializableExtra("event");
        long hoursToEvent = (int) TimeUnit.MILLISECONDS.toMinutes(e.getEventTimeInMillis() - System.currentTimeMillis()) / 60;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo_android)
                        .setContentTitle(e.getTitle())
                        .setContentText("Event starting in: " + hoursToEvent + "h");

        Intent resultIntent = new Intent(context, DetailActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DetailActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationsManager mNotificationManager = (NotificationsManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }*/
}
