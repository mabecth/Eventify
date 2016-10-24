package dat255.eventify.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dat255.eventify.R;
import dat255.eventify.model.Event;
import dat255.eventify.util.Constants;
import dat255.eventify.util.NotificationsUtil;

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

    private MyEventsManager() {
        storageManager = StorageManager.getInstance();
        allEvents = storageManager.getEvents();
        favorites = storageManager.getFavorites();
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
        return favorites;
    }

    public void modifyFavorites() {
        if (isFavorited()) {
            for (int i = 0; i < favorites.size(); i++) {
                if ( favorites.get(i).getId().equals(chosenEvent.getId())) {
                    favorites.remove(i);
                }
            }
        }
        else {
            favorites.add(chosenEvent);
            handleNotification(getApplicationContext(), chosenEvent);
        }
        storageManager.storeFavorites(favorites);
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


    public void handleNotification(Context context, Event event) {
        Intent alarmIntent = new Intent(context, NotificationsUtil.class);
        alarmIntent.putExtra("event", event);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 5000, pendingIntent);
    }


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
