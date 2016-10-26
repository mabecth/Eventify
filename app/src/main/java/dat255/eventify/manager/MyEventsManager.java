package dat255.eventify.manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dat255.eventify.R;
import dat255.eventify.model.Event;
import dat255.eventify.util.Constants;
import dat255.eventify.util.NotificationsUtil;
import dat255.eventify.util.SortByDate;

import static android.content.Context.ALARM_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Long on 21/10/2016.
 */

public class MyEventsManager {
    private List<Event> allEvents;
    private List<Event> favorites;
    private List<Event> filteredEvents;
    private List<String> allOrganization;
    private List<String> chosenOrgnization;
    private Event chosenEvent;
    private static MyEventsManager myEventsManager;
    private StorageManager storageManager;
    private NotificationsUtil notificationsUtil = new NotificationsUtil();
    private NotificationsManager notificationsManger = new NotificationsManager();
    private static int st = 0;


    private MyEventsManager() {
        storageManager = StorageManager.getInstance();
        allEvents = getEvents();
        favorites = getFavorites();
    }

    /**
     * Setup
     */
    public static MyEventsManager getInstance() {
        if (myEventsManager == null) {
            myEventsManager = new MyEventsManager();
        }
        return myEventsManager;
    }

    /**
     * MainList
     */
    public List<Event> getEvents(){
        allEvents = storageManager.getEvents();
        return allEvents;
    }

    /**
     * Detail
     */
    public void setChosenEvent(Event e) {
        chosenEvent = e;
    }

    public Event getChosenEvent() {
        return chosenEvent;
    }

    /**
     * Favorite
     */
    public List<Event> getFavorites() {
        favorites = storageManager.getFavorites();
        checkFavoriteExpried();
        return favorites;
    }

    private void checkFavoriteExpried(){
        getEvents();
        if (favorites.size() > 0)
        {
            for (int i = 0; i < favorites.size();i++){
                if (checkExpriedEvent(favorites.get(i)))
                {
                    favorites.remove(i);
                }
            }
        }
        SortByDate.sortDates(favorites);
        storageManager.storeFavorites(favorites);
    }

    private boolean checkExpriedEvent(Event e){
        boolean isExpried = true;
        for (int i = 0; i<allEvents.size();i++)
        {
            if (allEvents.get(i).getId().equals(e.getId()))
            {
                isExpried = false;
            }
        }
        return isExpried;
    }

    public void modifyFavorites() {
        if (isFavorited()) {
            for (int i = 0; i < favorites.size(); i++) {
                if ( favorites.get(i).getId().equals(chosenEvent.getId())) {
                    favorites.remove(i);
                }
            }
            SortByDate.sortDates(favorites);
            storageManager.storeFavorites(favorites);
        }
        else {
            if(st==0){
                chosenEvent.setTime("16:27");
            }
            if(st==1){
                chosenEvent.setTime("16:40");
            }
            if(st==2){
                chosenEvent.setTime("16:30");
            }
            st++;
            favorites.add(chosenEvent);
            SortByDate.sortDates(favorites);
            storageManager.storeFavorites(favorites);
            notificationsManger.createNotification();
        }
    }

    public boolean isFavorited() {
        boolean result = false;
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getId().equals(chosenEvent.getId())) {
                result = true;
            }
        }
        return  result;
    }

/*
    public void handleNotification(Context context, Event event) {
        long hoursToEvent = (int) TimeUnit.MILLISECONDS.toMinutes(event.getEventTimeInMillis() - System.currentTimeMillis()) / 60;


        Intent alarmIntent = new Intent(context, NotificationsUtil.class);
        alarmIntent.putExtra("event", event);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 5000, pendingIntent);
    }
*/

    /**
     * Filtering
     */
    public List<String> getAllOrganization(){
        allOrganization = new ArrayList<>();

        for (int i = 0; i<allEvents.size();i++) {
            if (!allOrganization.contains(allEvents.get(i).getOwner())) {
                allOrganization.add(allEvents.get(i).getOwner());
            }
        }
        return allOrganization;
    }

    public void setChosenOrgnization(List<String> listOfOrgnz)
    {
        chosenOrgnization = listOfOrgnz;
    }

    public List<Event> getFilteredEvents()
    {
        filteredEvents = new ArrayList<>();

        for (int i = 0;i < allEvents.size(); i++) {
            if(chosenOrgnization.contains(allEvents.get(i).getOwner())) {
                filteredEvents.add(allEvents.get(i));
            }
        }

        if (filteredEvents.size() == 0) {
            return allEvents;
        }
        else return filteredEvents;
    }
}
