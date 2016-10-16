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
        event.setDate("2016/10/20");
        event.setTime("10:00");
        event.setNbrAttending("13");

        assertEquals("Title", event.getTitle());
        assertEquals("Place", event.getPlace());
        assertEquals("2016/10/20", event.getDate());
        assertEquals("10:00", event.getTime());
        assertEquals("13", event.getNbrAttending());

    }
}
