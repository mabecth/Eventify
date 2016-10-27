package dat255.eventify.eventutil;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.helper.SortByDate;
import dat255.eventify.manager.StorageManager;
import dat255.eventify.model.Event;

public class MyEventsManger {
    private List<Event> allEvents;
    private List<Event> favorites;
    private List<Event> filteredEvents;
    private List<String> allOrganization;
    private List<String> chosenOrgnization;
    private Event chosenEvent;
    private static MyEventsManger myEventsManager;
    private StorageManager storageManager;
    private NotificationUtil notificationsManger = new NotificationUtil();

    private MyEventsManger() {
        storageManager = StorageManager.getInstance();
        allEvents = getEvents();
        favorites = getFavorites();
    }

    /**
     * Setup
     */
    public static MyEventsManger getInstance() {
        if (myEventsManager == null) {
            myEventsManager = new MyEventsManger();
        }
        return myEventsManager;
    }

    /**
     * MainList
     */
    public List<Event> getEvents() {
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

    private void checkFavoriteExpried() {
        getEvents();
        if (favorites.size() > 0) {
            for (int i = 0; i < favorites.size(); i++) {
                if (checkExpriedEvent(favorites.get(i))) {
                    favorites.remove(i);
                }
            }
        }
        SortByDate.sortDates(favorites);
        storageManager.storeFavorites(favorites);
    }

    private boolean checkExpriedEvent(Event e) {
        boolean isExpried = true;
        for (int i = 0; i < allEvents.size(); i++) {
            if (allEvents.get(i).getId().equals(e.getId())) {
                isExpried = false;
            }
        }
        return isExpried;
    }

    //adds and removes favorites from storage. Also creates a notification when a new favorite is added
    public void modifyFavorites() {
        if (isFavorited()) {
            for (int i = 0; i < favorites.size(); i++) {
                if (favorites.get(i).getId().equals(chosenEvent.getId())) {
                    favorites.remove(i);
                }
            }
            SortByDate.sortDates(favorites);
            storageManager.storeFavorites(favorites);
        } else {
            favorites.add(chosenEvent);
            SortByDate.sortDates(favorites);
            storageManager.storeFavorites(favorites);
            if (storageManager.getSettings().get("notification") == 1) {
                notificationsManger.createNotification();
            }
        }
    }

    public boolean isFavorited() {
        boolean result = false;
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getId().equals(chosenEvent.getId())) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Filtering
     */
    public List<String> getAllOrganization() {
        allOrganization = new ArrayList<>();

        for (int i = 0; i < allEvents.size(); i++) {
            if (!allOrganization.contains(allEvents.get(i).getOwner())) {
                allOrganization.add(allEvents.get(i).getOwner());
            }
        }
        return allOrganization;
    }

    public void setChosenOrgnization(List<String> listOfOrgnz) {
        chosenOrgnization = listOfOrgnz;
    }

    public List<Event> getFilteredEvents() {
        filteredEvents = new ArrayList<>();

        for (int i = 0; i < allEvents.size(); i++) {
            if (chosenOrgnization.contains(allEvents.get(i).getOwner())) {
                filteredEvents.add(allEvents.get(i));
            }
        }

        if (filteredEvents.size() == 0) {
            return allEvents;
        } else return filteredEvents;
    }
}
