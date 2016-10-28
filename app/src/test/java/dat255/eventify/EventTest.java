package dat255.eventify;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import dat255.eventify.model.Event;

public class EventTest {
    private Event event;

    //Initializing values before each test
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

    //testing if Latitude is set correct and can handle null
    @Test
    public void getLatitude() throws Exception {
        assertEquals(12.3145, event.getLatitude(), 0);

        event.setLatitude(null);
        assertEquals(0.0,event.getLatitude(),0);
    }

    //testing if Longitude is set correct and can handle null
    @Test
    public void getLongitude() throws Exception {
        assertEquals(0.0, event.getLongitude(), 0);


        event.setLongitude("13.43");
        assertEquals(13.43, event.getLongitude(), 0);
    }

    //testing if time to millis converter can andle both null input and catch exception when parsing
    @Test
    public void getEventTimeInMillis(){
        assertNotEquals(null,event.getEventTimeInMillis());
        event.setTime("hello");
        assertEquals(0,event.getEventTimeInMillis());
    }

    //testing if logic works in switch state and can handle unknown date types
    @Test
    public void getMonth() throws Exception {
        assertEquals("Oct", event.getMonth());

        event.setDate("2016-11-20");
        assertEquals("Nov",event.getMonth());

        event.setDate("hello there");
        assertEquals("Other",event.getMonth());
    }

    //testing the rest of the classes methods
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
