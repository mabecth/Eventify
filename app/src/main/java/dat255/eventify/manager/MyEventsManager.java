package dat255.eventify.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dat255.eventify.R;
import dat255.eventify.model.Event;
import dat255.eventify.util.Constants;

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

    public MyEventsManager() {
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
        else favorites.add(chosenEvent);
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
