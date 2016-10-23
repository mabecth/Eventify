package dat255.eventify.manager;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.model.Event;

/**
 * Created by Long on 21/10/2016.
 */

public class MyEventsManager {
    private List<Event> allEvents;
    private List<Event> favorites;
    private List<String> allOrganization;
    private List<String> activeOrganz;
    private List<String> inactiveOrgnz;
    private Event chosenEvent;
    private static MyEventsManager myEventsManager;
    private StorageManager storageManager;

    public MyEventsManager() {
        storageManager = StorageManager.getInstance();
        if(storageManager.getFavorites() == null) {
            favorites = new ArrayList<>();
        }
        else favorites = storageManager.getFavorites();

        allEvents = storageManager.getEvents();
        chosenEvent = new Event();
    }

    public MyEventsManager getInstance() {
        if (myEventsManager == null) {
            myEventsManager = new MyEventsManager();
        }
         return myEventsManager;
    }

    public void updateFromStorage(){
        allEvents = storageManager.getEvents();
        favorites = storageManager.getFavorites();
    }

    /**
    //Detail
    **/
     public void setChosenEvent(Event e) {
        chosenEvent = e;
    }

    public Event getChosenEvent() {
        return chosenEvent;
    }

    /**
    //Favorite
    **/
    public List<Event> getFavorites() {
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
        storageManager.storeFavorites();
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
    // Filtering
    **/
    public List<String> getAllOrganization() {
        allOrganization = new ArrayList<>();
        return allOrganization;
    }

    public List<String> getActiveOrganz(){
        activeOrganz = new ArrayList<>();

        for (int i = 0; i<allEvents.size();i++) {
            if (!activeOrganz.contains(allEvents.get(i).getOwner())) {
                activeOrganz.add(allEvents.get(i).getOwner());
            }
        }
        return  activeOrganz;
    }

    public List<String> getInactiveOrgnz()
    {
        activeOrganz = getActiveOrganz();
        inactiveOrgnz = new ArrayList<>();

        for (int i = 0;i<allOrganization.size();i++) {
            if (!activeOrganz.contains(allOrganization.get(i))) {
                inactiveOrgnz.add(allOrganization.get(i));
            }
        }
        return inactiveOrgnz;
    }

    public List<Event> getFilteredEvents(List<String> listOfOrgnz)
    {
        List<Event> filteredEvents = new ArrayList<>();

        for (int i = 0;i < allEvents.size(); i++) {
            if(listOfOrgnz.contains(allEvents.get(i).getOwner())) {
                filteredEvents.add(allEvents.get(i));
            }
        }

        if (filteredEvents.size() == 0) {
            return allEvents;
        }
        else return filteredEvents;
    }
}
