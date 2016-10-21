package dat255.eventify;

import org.junit.Test;
import static org.junit.Assert.*;

import dat255.eventify.model.Event;

public class EventTest {
    @Test
    public void eventClassTest() throws Exception{
        Event event = new Event();
        event.setTitle("Title");
        event.setPlace("Place");
        event.setDate("2016-10-20");
        event.setTime("10:00");
        event.setNbrAttending("13");
        event.setOwner("Organisation");
        event.setId("123456");
        event.setDesc("Description");
        event.setCover("url..");
        event.setDistance("14");
        event.setLatitude("12.3145");

        assertEquals("Title", event.getTitle());
        assertEquals("Place", event.getPlace());
        assertEquals("2016-10-20", event.getDate());
        assertEquals("10:00", event.getTime());
        assertEquals("13", event.getNbrAttending());
        assertEquals("Organisation", event.getOwner());
        assertEquals("123456", event.getId());
        assertEquals("Description", event.getDesc());
        assertEquals("url..", event.getCover());
        assertEquals("14", event.getDistance());
        assertEquals(12.3145, event.getLatitude(), 0);
        assertEquals(0.0, event.getLongitude(), 0);
        assertEquals("Oct", event.getMonth());

    }
}
