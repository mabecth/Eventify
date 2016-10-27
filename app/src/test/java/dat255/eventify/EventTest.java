package dat255.eventify;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

import dat255.eventify.model.Event;

public class EventTest {
    private Event event;
    @Before
    public void setUp(){
        event = new Event();
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
    }

    @Test
    public void getLatitude() throws Exception {
        assertEquals(12.3145, event.getLatitude(), 0);

        event.setLatitude(null);
        assertEquals(0.0,event.getLatitude(),0);
    }

    @Test
    public void getLongitude() throws Exception {
        assertEquals(0.0, event.getLongitude(), 0);


        event.setLongitude("13.43");
        assertEquals(13.43, event.getLongitude(), 0);
    }

    @Test
    public void getEventTimeInMillis(){
        assertNotEquals(null,event.getEventTimeInMillis());
        event.setTime("hej");
        assertEquals(0,event.getEventTimeInMillis());
    }

    @Test
    public void getMonth() throws Exception {
        assertEquals("Oct", event.getMonth());

        event.setDate("2016-11-20");
        assertEquals("Nov",event.getMonth());

        event.setDate("hello there");
        assertEquals("Other",event.getMonth());
    }

    @Test
    public void eventClassTest() throws Exception {
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
    }
}
