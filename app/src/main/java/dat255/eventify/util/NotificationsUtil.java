package dat255.eventify.util;

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
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
