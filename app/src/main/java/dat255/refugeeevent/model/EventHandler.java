package dat255.refugeeevent.model;

import java.util.ArrayList;
import dat255.refugeeevent.helpers.SortByDate;

public class EventHandler {
    private static EventHandler ourInstance = new EventHandler();

    private ArrayList<Event> events;

    private EventHandler() {
        events = new ArrayList<>();

        //TEST ----------
        Event testEvent = new Event();
        testEvent.setTitle("Football & hangout");
        testEvent.setPlace("Heden");
        testEvent.setTime("15:00");
        testEvent.setDate("17/08/2017");
        testEvent.setDesc("Hi everyone!");
        testEvent.setNbrAttending(203);

        Event test2 = new Event();
        test2.setTitle("Come and meet us");
        test2.setPlace("Lindome");
        test2.setTime("18:00");
        test2.setDate("17/08/2017");
        test2.setDesc("Hi everyone!");
        test2.setNbrAttending(20);

        Event test3 = new Event();
        test3.setTitle("Game of thrones & chill");
        test3.setPlace("Lindome");
        test3.setTime("18:00");
        test3.setDate("17/08/2017");
        test3.setDesc("Hi everyone!");
        test3.setNbrAttending(1002);

        events.add(test2);
        events.add(testEvent);
        events.add(test3);

        //date must be formatted dd/mm/yyyy
        SortByDate.sortDates(events);
        // --------------
    }

    public static EventHandler getInstance() {
        return ourInstance;
    }

    public Event getEventAt(int index){
        return events.get(index);
    }

    public ArrayList<Event> getEvents(){
        return events;
    }
}
