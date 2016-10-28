package dat255.eventify.eventutil;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.manager.StorageManager;
import dat255.eventify.mock.MockedContext;
import dat255.eventify.model.Event;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Kotex on 28/10/2016.
 */

public class FilteringTest {
    private Event event1;
    private Event event2;
    private Event event3;
    private Event event4;
    private String organisation1;
    private String organisation2;
    private String organisation3;

    private List<Event> listOfEvent;
    private MyEventsManger eventsManager;
    private StorageManager storageManager;
    private List<String> chosenOrgnz;

    @Before
    public void setUp(){
        storageManager = StorageManager.getInstance();
        storageManager.setContext(new MockedContext());

        organisation1 = "Organisation1";
        organisation2 = "Organisation2";
        organisation3 = "Organisation3";

        event1 = new Event();
        event1.setId("1");
        event1.setTitle("First Event");
        event1.setDate("2020-12-12");
        event1.setPlace("Place");
        event1.setTime("10:00");
        event1.setNbrAttending("3");
        event1.setOwner(organisation1);
        event1.setDesc("Description");
        event1.setCover("url..");
        event1.setDistance("14");
        event1.setLatitude("12.3145");
        event1.setLongitude("42.55");

        event2 = new Event();
        event2.setId("2");
        event2.setTitle("Second Event");
        event2.setDate("2020-08-12");
        event2.setPlace("Place");
        event2.setTime("12:00");
        event2.setNbrAttending("200");
        event2.setOwner(organisation1);
        event2.setDesc("Description");
        event2.setCover("url..");
        event2.setDistance("14");
        event2.setLatitude("12.3145");
        event2.setLongitude("42.55");

        event3 = new Event();
        event3.setId("3");
        event3.setTitle("Third Event");
        event3.setDate("2020-12-08");
        event3.setPlace("Place");
        event3.setTime("08:00");
        event3.setNbrAttending("2");
        event3.setOwner(organisation2);
        event3.setDesc("Description");
        event3.setCover("url..");
        event3.setDistance("14");
        event3.setLatitude("12.3145");
        event3.setLongitude("42.55");

        event4 = new Event();
        event4.setId("4");
        event4.setTitle("Fourth Event");
        event4.setDate("2020-10-10");
        event4.setPlace("Place");
        event4.setTime("20:00");
        event4.setNbrAttending("23");
        event4.setOwner(organisation3);
        event4.setDesc("Description");
        event4.setCover("url..");
        event4.setDistance("14");
        event4.setLatitude("12.3145");
        event4.setLongitude("42.55");

        listOfEvent = new ArrayList<>();
        chosenOrgnz = new ArrayList<>();
        eventsManager = MyEventsManger.getInstance();

        listOfEvent.add(event1);
        listOfEvent.add(event2);
        listOfEvent.add(event3);
        listOfEvent.add(event4);
        storageManager.storeEvents(listOfEvent);

    }

    @Test
    public void filteringTest() {
        chosenOrgnz.add(organisation1);
        eventsManager.setChosenOrgnization(chosenOrgnz);

        assertEquals("1",eventsManager.getFilteredEvents().get(0).getId());
        assertEquals("2",eventsManager.getFilteredEvents().get(1).getId());
    }


}
