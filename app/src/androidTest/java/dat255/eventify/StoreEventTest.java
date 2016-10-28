package dat255.eventify;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.manager.StorageManager;
import dat255.eventify.model.Event;

import static junit.framework.Assert.assertEquals;

public class StoreEventTest {

    /*
    Test that puts a hardcoded event in a list and uses methods for checking if the test is
    succesfully added to the list.
     */

    private List<Event> myTestEvents = new ArrayList<>();

    Event event;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("dat255.eventify", appContext.getPackageName());

        StorageManager.getInstance().setContext(appContext);

        event = new Event();
        event.setId("1");

        myTestEvents.add(event);

        StorageManager.getInstance().storeEvents(myTestEvents);

        assertEquals("1", StorageManager.getInstance().getEvents().get(0).getId());
    }
}
