package dat255.eventify.eventutil;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.mock.MockedContext;
import dat255.eventify.manager.StorageManager;
import dat255.eventify.model.Event;

import static junit.framework.Assert.assertEquals;

public class FavoriteTest {

    /*
    Test that tests the addToFavorite logic.
     */

    private Event event1;
    private Event event2;
    private MyEventsManger myEventsManger;
    private StorageManager storageManager;
    private List<Event> eventList;

    @Before
    public void setUp() {
        storageManager = StorageManager.getInstance();
        storageManager.setContext(new MockedContext());


        event1 = new Event();
        event1.setId("1");
        event1.setTitle("Event Favorite");
        event1.setDate("2020-12-12");
        event1.setPlace("Place");
        event1.setTime("10:00");
        event1.setNbrAttending("13");
        event1.setOwner("Organisation");
        event1.setDesc("Description");
        event1.setCover("url..");
        event1.setDistance("14");
        event1.setLatitude("12.3145");
        event1.setLongitude("42.55");
        eventList = new ArrayList<>();
        eventList.add(event1);
        storageManager.storeFavorites(eventList);

        myEventsManger = MyEventsManger.getInstance();
    }

    @Test
    public void useAppContext() throws Exception {

        // Context of the app under test.


        myEventsManger.setChosenEvent(event1);


        myEventsManger.modifyFavorites();

        assertEquals(false, myEventsManger.isFavorited());

        event2 = new Event();
        event2.setId("2");
        event2.setTitle("Event Favorite 2");
        event2.setDate("2019-12-12");
        event2.setPlace("Place");
        event2.setTime("10:00");
        event2.setNbrAttending("13");
        event2.setOwner("Organisation");
        event2.setDesc("Description");
        event2.setCover("url..");
        event2.setDistance("14");
        event2.setLatitude("12.3145");
        event2.setLongitude("44.23");

        eventList.add(event2);
        storageManager.storeFavorites(eventList);

        myEventsManger.setChosenEvent(event2);

        assertEquals("2", myEventsManger.getFavorites().get(0).getId());
    }
}
